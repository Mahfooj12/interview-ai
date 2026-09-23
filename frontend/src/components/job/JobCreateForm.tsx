"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useQuery } from "@tanstack/react-query";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
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
import { useCreateJobFromText } from "@/hooks/useJobs";
import { useResumes } from "@/hooks/useResumes";

const schema = z.object({
  title: z.string().max(120).optional(),
  company: z.string().max(120).optional(),
  rawText: z
    .string()
    .min(30, "Job description must be at least 30 characters")
    .max(40000, "Job description is too long"),
  resumeId: z.string().optional(),
});

type FormValues = z.infer<typeof schema>;

export function JobCreateForm() {
  const router = useRouter();
  const create = useCreateJobFromText();
  const { data: resumes } = useResumes();

  const form = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { title: "", company: "", rawText: "", resumeId: "" },
  });

  const onSubmit = async (values: FormValues) => {
    const result = await create.mutateAsync({
      rawText: values.rawText,
      title: values.title || undefined,
      company: values.company || undefined,
      resumeId: values.resumeId || undefined,
    });
    router.push(`/jobs/${result.id}`);
  };

  return (
    <Card>
      <CardContent className="p-6">
        <Form {...form}>
          <form
            onSubmit={form.handleSubmit(onSubmit)}
            className="space-y-4"
            noValidate
          >
            <div className="grid gap-4 sm:grid-cols-2">
              <FormField
                control={form.control}
                name="title"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Role title (optional)</FormLabel>
                    <FormControl>
                      <Input placeholder="Backend Engineer" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="company"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Company (optional)</FormLabel>
                    <FormControl>
                      <Input placeholder="Acme Inc." {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            <FormField
              control={form.control}
              name="rawText"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Job description</FormLabel>
                  <FormControl>
                    <Textarea
                      rows={10}
                      placeholder="Paste the job description here…"
                      {...field}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {resumes && resumes.length > 0 ? (
              <FormField
                control={form.control}
                name="resumeId"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Match against resume (optional)</FormLabel>
                    <FormControl>
                      <select
                        className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                        {...field}
                      >
                        <option value="">— Select a resume —</option>
                        {resumes.map((r) => (
                          <option key={r.id} value={r.id}>
                            {r.fileName} {r.parsedName ? `(${r.parsedName})` : ""}
                          </option>
                        ))}
                      </select>
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            ) : null}

            <Button type="submit" disabled={create.isPending}>
              {create.isPending ? <Spinner /> : "Create job description"}
            </Button>
          </form>
        </Form>
      </CardContent>
    </Card>
  );
}