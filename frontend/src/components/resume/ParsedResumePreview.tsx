"use client";

import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import type { ParsedResume } from "@/types/resume";

interface ParsedResumePreviewProps {
  parsed: ParsedResume;
}

export function ParsedResumePreview({ parsed }: ParsedResumePreviewProps) {
  return (
    <div className="space-y-4">
      <Card>
        <CardHeader>
          <CardTitle className="text-base">Profile</CardTitle>
        </CardHeader>
        <CardContent className="space-y-1 text-sm">
          <p className="font-medium">{parsed.name ?? "—"}</p>
          <p className="text-muted-foreground">
            {parsed.email ?? "—"} · {parsed.phone ?? "—"}
          </p>
          {parsed.summary ? (
            <p className="mt-2 text-muted-foreground">{parsed.summary}</p>
          ) : null}
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Skills</CardTitle>
        </CardHeader>
        <CardContent className="space-y-3">
          <SkillGroup label="Skills" items={parsed.skills} />
          <SkillGroup label="Languages" items={parsed.languages} />
          <SkillGroup label="Frameworks" items={parsed.frameworks} />
          <SkillGroup label="Databases" items={parsed.databases} />
          <SkillGroup label="Tools" items={parsed.tools} />
        </CardContent>
      </Card>

      {parsed.projects.length > 0 ? (
        <Card>
          <CardHeader>
            <CardTitle className="text-base">Projects</CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            {parsed.projects.map((project, idx) => (
              <div key={idx} className="space-y-1">
                <p className="font-medium">{project.name}</p>
                <p className="text-sm text-muted-foreground">
                  {project.description}
                </p>
                <div className="flex flex-wrap gap-1">
                  {project.tech.map((t) => (
                    <Badge key={t} variant="outline">
                      {t}
                    </Badge>
                  ))}
                </div>
                {idx < parsed.projects.length - 1 ? (
                  <Separator className="mt-3" />
                ) : null}
              </div>
            ))}
          </CardContent>
        </Card>
      ) : null}

      {parsed.technicalClaims.length > 0 ? (
        <Card>
          <CardHeader>
            <CardTitle className="text-base">Technical claims</CardTitle>
          </CardHeader>
          <CardContent className="space-y-2 text-sm">
            {parsed.technicalClaims.map((claim, idx) => (
              <div key={idx}>
                <span className="font-medium">{claim.topic}</span>{" "}
                <span className="text-muted-foreground">
                  — {claim.evidence}
                </span>
              </div>
            ))}
          </CardContent>
        </Card>
      ) : null}
    </div>
  );
}

function SkillGroup({ label, items }: { label: string; items: string[] }) {
  if (!items || items.length === 0) return null;
  return (
    <div>
      <p className="mb-1 text-xs font-medium uppercase tracking-wide text-muted-foreground">
        {label}
      </p>
      <div className="flex flex-wrap gap-1">
        {items.map((item) => (
          <Badge key={item} variant="secondary">
            {item}
          </Badge>
        ))}
      </div>
    </div>
  );
}