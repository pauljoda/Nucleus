# Paul Guidance for Nucleus 26.1 Migration

Date: 2026-06-09

Paul clarified:

- This migration should be a clean, full break for NeoForge 26.1.
- Assume fresh launch only: no previous saves, old stacks, old worlds, or old consuming-mod compatibility need to be preserved.
- Remove one-time migrations, legacy patching, compatibility bridges, deprecated old behavior, and old-system surfaces wherever possible.
- Nucleus is only consumed by Paul’s mods, so downstream breakage is acceptable and expected; consuming mods will be updated afterward.
- Align deeply with modern official NeoForge 26.1 patterns and documentation.
- Any stubs, no-op replacements, workarounds, and incomplete areas should be built out as real 26.1 implementations.
- Connected textures and similar library features should be migrated/rebuilt, not pushed to dependent mods by default.
- When a paradigm has changed, migrate fully to the new paradigm. Example: old NBT/CompoundTag-based core APIs should move to ValueInput/ValueOutput/data components, and old CompoundTag compatibility bridges should be removed rather than kept.

Practical implementation direction:

1. Remove deprecated compatibility APIs that exist only for old Forge/NeoForge/downstream source compatibility.
2. Remove one-time legacy data migrations from CUSTOM_DATA or old NBT layouts.
3. Remove primary exposure of old `IEnergyStorage`, `IItemHandler`, `IFluidHandler`, `FluidTank`, old `CompoundTag` Savable methods, and old GUI helper overloads where they only preserve previous behavior.
4. Prefer modern APIs as the only APIs:
   - ValueInput / ValueOutput for save/load.
   - Data components for item/block item persistent state.
   - NeoForge transfer APIs for energy, item, and fluid movement.
   - GuiGraphicsExtractor/render-state paths for GUI.
   - CustomPacketPayload.Type + StreamCodec for networking.
5. Do not preserve old saves/items/world data unless vanilla/NeoForge itself requires the type internally.
6. Update `UPGRADE_NOTES.md` with breaking changes and removed legacy surfaces.
7. Continue verification with Java 25 compile/build/runClient smoke, but defer detailed dependent-mod runtime testing until after the clean-break state is reached.
