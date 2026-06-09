You are a Codex audit worker inside `/home/hermes/minecraft-modding/Nucleus-26.1` on branch `update/neoforge-26.1`.

Read `UPGRADE_NOTES.md`, `WORKER_PROMPT.md`, and the current git diff against the baseline. Paul specifically requested identification of all areas that are not actually updated, including stubs, no-op replacements, temporary workarounds, incomplete semantic migrations, APIs that compile but may be wrong, and runtime warnings.

Your task:
1. Audit the current diff and source for stubs/no-ops/TODOs/compile-preserving replacements/incomplete semantic migrations/runtime-warning sources.
2. Ensure `UPGRADE_NOTES.md` contains a section exactly named `## Areas needing Paul review / incomplete migrations`.
3. In that section, list file paths, the workaround/incomplete area, why it matters, and suggested questions/options for Paul.
4. Do not remove existing verification history. Keep notes concise but specific.
5. Run at least `JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" ./gradlew compileJava --stacktrace` after note/source changes if you touch source; if you only update notes, no build is required but say so.
6. Do not publish, push, schedule cron jobs, or ask interactive questions.
7. Print line-start markers in tmux output: `STATUS:`, `BLOCKED:`, `QUESTION_FOR_PAUL:`, or `READY_FOR_REVIEW:`.

Prefer notes-only audit unless you find a small, obvious correctness fix that does not alter public API/custom GUI behavior. If a true Paul decision is needed, write it as `QUESTION_FOR_PAUL:` in tmux and in `UPGRADE_NOTES.md`.