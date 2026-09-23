"use client";

import { useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent } from "@/components/ui/card";
import { Spinner } from "@/components/ui/spinner";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { useResumes } from "@/hooks/useResumes";
import { useJobs } from "@/hooks/useJobs";
import { useCreateInterview } from "@/hooks/useInterview";
import { cn } from "@/lib/utils";
import type {
  InterviewDifficulty,
  InterviewMode,
  InterviewPersonality,
  InterviewType,
} from "@/types/interview";

const schema = z.object({
  resumeId: z.string().optional(),
  jobDescriptionId: z.string().optional(),
  type: z.enum([
    "TECHNICAL",
    "HR",
    "RESUME",
    "PROJECT",
    "SYSTEM_DESIGN",
    "BEHAVIORAL",
  ]),
  mode: z.enum(["TEXT", "VOICE", "VIDEO"]),
  difficulty: z.enum(["BEGINNER", "INTERMEDIATE", "ADVANCED"]),
  personality: z.enum([
    "FRIENDLY",
    "STRICT",
    "PROFESSIONAL",
    "TECHNICAL",
    "HR_MANAGER",
  ]),
  focusTopic: z.string().optional(),
});

type FormValues = z.infer<typeof schema>;

const TYPES: { value: InterviewType; label: string }[] = [
  { value: "TECHNICAL", label: "Technical" },
  { value: "HR", label: "HR" },
  { value: "RESUME", label: "Resume deep-dive" },
  { value: "PROJECT", label: "Project" },
  { value: "SYSTEM_DESIGN", label: "System design" },
  { value: "BEHAVIORAL", label: "Behavioral" },
];

const MODES: { value: InterviewMode; label: string }[] = [
  { value: "TEXT", label: "Text" },
  { value: "VOICE", label: "Voice" },
  { value: "VIDEO", label: "Video" },
];

const DIFFICULTIES: { value: InterviewDifficulty; label: string }[] = [
  { value: "BEGINNER", label: "Beginner" },
  { value: "INTERMEDIATE", label: "Intermediate" },
  { value: "ADVANCED", label: "Advanced" },
];

const PERSONALITIES: { value: InterviewPersonality; label: string }[] = [
  { value: "FRIENDLY", label: "Friendly" },
  { value: "STRICT", label: "Strict" },
  { value: "PROFESSIONAL", label: "Professional" },
  { value: "TECHNICAL", label: "Technical" },
  { value: "HR_MANAGER", label: "HR Manager" },
];

export function InterviewSetupForm() {
  const router = useRouter();
  const search = useSearchParams();
  const focusFromQuery = search?.get("focus") ?? "";
  const { data: resumes } = useResumes();
  const { data: jobs } = useJobs();
  const create = useCreateInterview();

  const form = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      resumeId: "",
      jobDescriptionId: "",
      type: "TECHNICAL",
      mode: "TEXT",
      difficulty: "INTERMEDIATE",
      personality: "PROFESSIONAL",
      focusTopic: focusFromQuery,
    },
  });

  useEffect(() => {
    if (focusFromQuery) {
      form.setValue("focusTopic", focusFromQuery);
      form.setValue("type", "TECHNICAL");
    }
  }, [focusFromQuery, form]);

  const onSubmit = async (values: FormValues) => {
    const interview = await create.mutateAsync({
      resumeId: values.resumeId || undefined,
      jobDescriptionId: values.jobDescriptionId || undefined,
      type: values.type,
      mode: values.mode,
      difficulty: values.difficulty,
      personality: values.personality,
      focusTopic: values.focusTopic || undefined,
    });
    const modePath = values.mode.toLowerCase();
    const target =
      modePath === "text"
        ? `/interview/${interview.id}`
        : `/interview/${interview.id}/${modePath}`;
    router.push(target);
    //router.push(`/interview/${interview.id}`);
  };

  return (
    <Card>
      <CardContent className="p-6">
        <Form {...form}>
          <form
            onSubmit={form.handleSubmit(onSubmit)}
            className="space-y-6"
            noValidate
          >
            <FormField
              control={form.control}
              name="resumeId"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Resume</FormLabel>
                  <FormControl>
                    <select
                      className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                      {...field}
                    >
                      <option value="">
                        Use primary resume (default)
                      </option>
                      {resumes?.map((r) => (
                        <option key={r.id} value={r.id}>
                          {r.fileName}
                        </option>
                      ))}
                    </select>
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="jobDescriptionId"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Job description (optional)</FormLabel>
                  <FormControl>
                    <select
                      className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                      {...field}
                    >
                      <option value="">None</option>
                      {jobs?.map((j) => (
                        <option key={j.id} value={j.id}>
                          {j.title ?? "Untitled"} {j.company ? `@ ${j.company}` : ""}
                        </option>
                      ))}
                    </select>
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FieldGroup
              label="Interview type"
              control={form.control}
              name="type"
              options={TYPES}
            />
            <FieldGroup
              label="Mode"
              control={form.control}
              name="mode"
              options={MODES}
            />
            <FieldGroup
              label="Difficulty"
              control={form.control}
              name="difficulty"
              options={DIFFICULTIES}
            />
            <FieldGroup
              label="Interviewer personality"
              control={form.control}
              name="personality"
              options={PERSONALITIES}
            />

            <FormField
              control={form.control}
              name="focusTopic"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Focus topic (optional)</FormLabel>
                  <FormControl>
                    <Input
                      placeholder="e.g. Spring Security"
                      {...field}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <Button type="submit" disabled={create.isPending}>
              {create.isPending ? <Spinner /> : "Start interview"}
            </Button>
          </form>
        </Form>
      </CardContent>
    </Card>
  );
}

function FieldGroup<T extends string>({
  label,
  control,
  name,
  options,
}: {
  label: string;
  control: ReturnType<typeof useForm<FormValues>>["control"];
  name: keyof FormValues;
  options: { value: T; label: string }[];
}) {
  return (
    <FormField
      control={control}
      name={name as never}
      render={({ field }) => (
        <FormItem>
          <FormLabel>{label}</FormLabel>
          <FormControl>
            <div className="flex flex-wrap gap-2">
              {options.map((option) => {
                const active = field.value === option.value;
                return (
                  <button
                    key={option.value}
                    type="button"
                    onClick={() => field.onChange(option.value)}
                    className={cn(
                      "rounded-md border px-3 py-1.5 text-sm transition-colors",
                      active
                        ? "border-primary bg-primary/10 text-primary"
                        : "border-border hover:bg-accent",
                    )}
                  >
                    {option.label}
                  </button>
                );
              })}
            </div>
          </FormControl>
          <FormMessage />
        </FormItem>
      )}
    />
  );
}