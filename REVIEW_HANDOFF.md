# Review Handoff Instructions

Paul note, 2026-06-09:

When the Nucleus 26.1 migration is ready for Paul to review, push the worktree branch so Paul can pull and test on his machine.

Current worktree:

- Path: `/home/hermes/minecraft-modding/Nucleus-26.1`
- Branch: `update/neoforge-26.1`
- Remote: `origin` -> `https://github.com/pauljoda/Nucleus.git`

Expected readiness before push:

1. `compileJava` passes with Java 25.
2. `build` passes with Java 25.
3. Headless `runClient` smoke reaches NeoForge/Minecraft mod loading/render-thread startup under Xvfb.
4. `UPGRADE_NOTES.md` clearly lists remaining known caveats and resolved stubs/workarounds.
5. No known placeholder/no-op implementations remain unless explicitly documented and accepted.
6. Commit the migration changes on `update/neoforge-26.1` and push:

```bash
git -C /home/hermes/minecraft-modding/Nucleus-26.1 status --short
git -C /home/hermes/minecraft-modding/Nucleus-26.1 add .
git -C /home/hermes/minecraft-modding/Nucleus-26.1 commit -m "Port Nucleus to NeoForge 26.1"
git -C /home/hermes/minecraft-modding/Nucleus-26.1 push -u origin update/neoforge-26.1
```

Do not push before the code is ready for review. Do not create a PR unless Paul asks for one.
