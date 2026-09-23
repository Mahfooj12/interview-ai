"use client";

/**
 * Thin wrapper around MediaRecorder that yields audio blobs and maintains
 * an AnalyserNode so callers can render a live waveform.
 */
export interface RecorderOptions {
  onData?: (blob: Blob) => void;
  onLevel?: (level: number) => void;
  mimeType?: string;
}

export class AudioRecorder {
  private mediaRecorder: MediaRecorder | null = null;
  private stream: MediaStream | null = null;
  private audioContext: AudioContext | null = null;
  private analyser: AnalyserNode | null = null;
  private source: MediaStreamAudioSourceNode | null = null;
  private chunks: BlobPart[] = [];
  private rafId: number | null = null;
  private readonly options: RecorderOptions;

  constructor(options: RecorderOptions = {}) {
    this.options = options;
  }

  async start(): Promise<void> {
    if (this.mediaRecorder) return;

    this.stream = await navigator.mediaDevices.getUserMedia({
      audio: {
        echoCancellation: true,
        noiseSuppression: true,
        autoGainControl: true,
      },
    });

    const mimeType =
      this.options.mimeType ??
      (MediaRecorder.isTypeSupported("audio/webm;codecs=opus")
        ? "audio/webm;codecs=opus"
        : "audio/webm");

    this.mediaRecorder = new MediaRecorder(this.stream, { mimeType });
    this.chunks = [];

    this.mediaRecorder.ondataavailable = (event) => {
      if (event.data.size > 0) {
        this.chunks.push(event.data);
      }
    };

    this.mediaRecorder.start(250);

    // Set up analyser for level metering
    this.audioContext = new AudioContext();
    this.source = this.audioContext.createMediaStreamSource(this.stream);
    this.analyser = this.audioContext.createAnalyser();
    this.analyser.fftSize = 1024;
    this.source.connect(this.analyser);
    this.startMetering();
  }

  private startMetering() {
    if (!this.analyser) return;
    const buffer = new Uint8Array(this.analyser.fftSize);
    const loop = () => {
      if (!this.analyser) return;
      this.analyser.getByteTimeDomainData(buffer);
      let sum = 0;
      for (let i = 0; i < buffer.length; i++) {
        const v = (buffer[i] - 128) / 128;
        sum += v * v;
      }
      const rms = Math.sqrt(sum / buffer.length);
      this.options.onLevel?.(Math.min(1, rms * 4));
      this.rafId = requestAnimationFrame(loop);
    };
    this.rafId = requestAnimationFrame(loop);
  }

  async stop(): Promise<Blob | null> {
    return new Promise((resolve) => {
      if (!this.mediaRecorder) {
        resolve(null);
        return;
      }
      this.mediaRecorder.onstop = () => {
        const blob = new Blob(this.chunks, {
          type: this.mediaRecorder?.mimeType ?? "audio/webm",
        });
        this.options.onData?.(blob);
        this.cleanup();
        resolve(blob);
      };
      this.mediaRecorder.stop();
    });
  }

  cancel() {
    if (this.mediaRecorder && this.mediaRecorder.state !== "inactive") {
      this.mediaRecorder.stop();
    }
    this.cleanup();
  }

  private cleanup() {
    if (this.rafId !== null) {
      cancelAnimationFrame(this.rafId);
      this.rafId = null;
    }
    this.source?.disconnect();
    this.analyser?.disconnect();
    this.stream?.getTracks().forEach((t) => t.stop());
    if (this.audioContext && this.audioContext.state !== "closed") {
      this.audioContext.close().catch(() => {});
    }
    this.mediaRecorder = null;
    this.stream = null;
    this.audioContext = null;
    this.analyser = null;
    this.source = null;
    this.chunks = [];
  }
}