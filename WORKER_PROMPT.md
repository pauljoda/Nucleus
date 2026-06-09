# Codex Worker Prompt: Nucleus NeoForge 26.1 Upgrade

You are a coding worker inside `/home/hermes/minecraft-modding/Nucleus-26.1`, a git worktree on branch `update/neoforge-26.1`.

Goal: update Nucleus from the current verified baseline (Minecraft 1.20.4 / NeoForge 20.4.189 / Java 17) toward current NeoForge 26.1 (latest observed `26.1.2.75`). Nucleus is a core library for Paul Davis's Minecraft mods.

Coordinator context is in `/home/hermes/minecraft-modding/COORDINATOR.md`; read it first.

Environment:

- Java 17: `/home/hermes/.local/jdks/jdk17`
- Java 21: `/home/hermes/.local/jdks/jdk21`
- Current baseline build command succeeded in the original worktree:
  `JAVA_HOME=/home/hermes/.local/jdks/jdk17 PATH="$JAVA_HOME/bin:$PATH" ./gradlew build --stacktrace`
- For MC 1.20.5+ use Java 21: `export JAVA_HOME=/home/hermes/.local/jdks/jdk21; export PATH="$JAVA_HOME/bin:$PATH"`
- GUI smoke can use `xvfb-run -a ./gradlew runClient`, but only after build succeeds; the process normally stays open, so use a timeout.

Important docs:

- NeoForge primers index: https://docs.neoforged.net/primer/docs/
- Starting current state: 1.20.4 -> 1.20.5 primer: https://docs.neoforged.net/primer/docs/1.20.5/
- Continue sequentially through latest per `/home/hermes/minecraft-modding/COORDINATOR.md`.
- Latest Maven metadata: NeoForge `26.1.2.75`; ModDevGradle `2.0.141`.

Instructions:

1. Inspect the current project and docs.
2. Prefer an incremental port path, but if a direct upgrade is tractable, document the reasoning.
3. Update Gradle wrapper/plugin/build files/resources/source as needed.
4. Run actual verification commands. At minimum, keep running `./gradlew build --stacktrace` with the appropriate Java until either it succeeds or you hit a clear blocker.
5. Do not publish, push, or create PRs.
6. Keep notes in `UPGRADE_NOTES.md` in the worktree: changes made, blockers, verification outputs, and remaining work.
7. Use these line-start markers in tmux output so Hermes can monitor:
   - `STATUS: ...`
   - `QUESTION_FOR_PAUL: ...`
   - `BLOCKED: ...`
   - `READY_FOR_REVIEW: ...`

If you are blocked by missing information or a decision only Paul can make, stop and print `QUESTION_FOR_PAUL:` with the exact question. If blocked by tooling/dependencies, print `BLOCKED:` with the exact command and error summary.
