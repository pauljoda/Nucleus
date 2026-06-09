# Paul Guidance for Nucleus 26.1 Migration

Date: 2026-06-09

Paul clarified:

- Any stubs, no-op replacements, workarounds, and areas that need to be built out should be built out rather than left as compatibility placeholders.
- Connected textures and similar library features should be migrated/rebuilt, not pushed to dependent mods by default.
- Nucleus is tied to the target game version and to mods that consume it. Breaking changes are acceptable when needed to align with modern NeoForge; consuming mods can be fixed afterward.
- Prefer following the official NeoForge documentation and version-to-version primers over preserving old Forge/NeoForge patterns.
- When a paradigm has changed, migrate fully to the new paradigm. Example: old NBT/CompoundTag-based APIs should move to the new ValueInput/ValueOutput/data component model rather than being kept as the core abstraction.

Practical implementation direction:

1. Replace stubs/no-ops with real 26.1 implementations.
2. Treat compile-only mechanical migrations as unfinished until the behavior is ported and verified.
3. Public API source compatibility is lower priority than correct NeoForge 26.1 architecture.
4. Keep a clear migration note for downstream mods explaining breaking changes.
5. Continue updating `UPGRADE_NOTES.md`, especially `Areas needing Paul review / incomplete migrations`, as items are resolved.
