# Nucleus NeoForge 26.1 Upgrade Notes

## Current status

STATUS: Partial feature migration. The project is configured for Minecraft `26.1.2`, NeoForge `26.1.2.75`, ModDevGradle `2.0.141`, Gradle `9.1.0`, and Java `25`. `compileJava` and `build` pass after replacing several compile-preserving stubs/no-ops, but the port still needs runtime validation on dependent mods.

## Latest migration pass on 2026-06-09 UTC

STATUS: Replaced `BaseBlockStateGenerator` stub with a real `DataProvider` implementation. `addConnectedTextureModels` now queues connected-texture blocks and `run` writes blockstate JSON containing all six `connected_*` properties plus a cube-all model for each block.

STATUS: Fixed connected texture behavior in `ConnectedTextureBlock`; it now connects to matching neighboring blocks like `UpdatingConnectedTextureBlock`, instead of requiring the neighbor position to be both empty and the same block.

STATUS: Moved the core `Savable` API to `ValueInput` / `ValueOutput`. The old `CompoundTag` methods remain only as deprecated bridges for downstream source migration.

STATUS: Migrated `InventoryContents`, `EnergyBank`, `InventoryHandler`, `EnergyHandler`, `EnergyAndItemHandler`, `EnergyAndFluidHandler`, `PanHandler`, `FluidHandler`, and `FluidAndItemHandler` persistence paths onto `ValueInput` / `ValueOutput`.

STATUS: Fixed an inverted fluid persistence bug where `FluidHandler` and `FluidAndItemHandler` were writing tank data in `load` and reading it in `saveAdditional`.

STATUS: Replaced item/energy helper no-ops with 26.1 transfer-capability logic:
- `EnergyUtils#distributePowerToFaces` and `consumePowerFromFaces` now query `Capabilities.Energy.BLOCK` and use `EnergyHandler` transactions.
- `EnergyUtils#addToolTipInfo(ItemStack, ...)` now queries `Capabilities.Energy.ITEM` via `ItemAccess`.
- `InventoryUtils#moveItemInto` now uses `Capabilities.Item.BLOCK` and `ResourceHandler<ItemResource>` transactions for sided block-entity transfers.

STATUS: Replaced `GuiHelper#renderFluid` no-op with a `GuiGraphicsExtractor` implementation that resolves fluid sprites through `AtlasManager` / `SpriteId` and draws scaled tank contents.

STATUS: Updated `pack.mcmeta` with explicit 26.1 `min_format` and `max_format` metadata. A later smoke showed `supported_formats` is deprecated for pack format 65+, so it was removed.

STATUS: Removed obsolete `@OnlyIn` annotations from Nucleus tooltip interfaces and `EnergyUtils`.

STATUS: Restored part of `BaseLootTableGenerator#createStandardTable` block-entity drop behavior using 26.1 loot functions. It now copies block entity `CUSTOM_NAME`, `LOCK`, and `CONTAINER_LOOT` data components from `LootContextParams.BLOCK_ENTITY` and still writes dynamic container contents into `DataComponents.CONTAINER`.

STATUS: Updated wrench block-entity item transfer in `LevelUtils` to store `saveCustomOnly` data in `DataComponents.BLOCK_ENTITY_DATA`, avoiding full metadata/id/position data in item components while preserving custom block entity state for `TypedEntityData#loadInto`.

STATUS: Fixed `EnergyContainingItem` behavior and exposed a first-class NeoForge 26.1 `transfer.energy.EnergyHandler` implementation with transactional rollback. The legacy `IEnergyStorage` wrapper now passes the simulation flag correctly instead of inverting it.

STATUS: Fixed `InventoryHandlerItem` persistence so slot changes write back to the stack's component data, and exposed it as a first-class `ResourceHandler<ItemResource>` with transaction rollback around the existing slot rules.

STATUS: Fixed `InventoryHolderCapability#isValidSlot` to reject `slot == size`, preventing out-of-range list access.

STATUS: Registered typed Nucleus item data components:
- `nucleus_pauljoda:item_energy` stores item energy as an immutable `EnergyStorageComponent` with persistent and network codecs.
- `nucleus_pauljoda:item_inventory` stores item inventories as an immutable `ItemInventoryComponent` with persistent and network codecs.

STATUS: Migrated `EnergyContainingItem` and `InventoryHandlerItem` away from primary `DataComponents.CUSTOM_DATA` storage. They now read/write the typed Nucleus components, use typed component snapshots for transfer rollback, and only read legacy `CUSTOM_DATA` once to migrate old stacks.

STATUS: Exposed block-entity fluid tanks through first-class NeoForge 26.1 `ResourceHandler<FluidResource>` handlers via `FluidHandler#getFluidResourceHandler` and `FluidAndItemHandler#getFluidResourceHandler`. The deprecated `IFluidHandler` methods now delegate through transactional fluid-resource operations.

STATUS: Fixed fluid tank transfer behavior while adding the 26.1 transfer handler:
- `FluidHandler` and `FluidAndItemHandler` no longer call the underlying `FluidTank` twice during `fill` or `drain(int, ...)`.
- Filled input tanks can now accept additional matching fluid instead of only empty tanks.
- `drain(FluidStack, ...)` now drains the requested fluid resource instead of falling back to the first output tank with any fluid.

STATUS: `EnergyBank` now implements the NeoForge 26.1 `transfer.energy.EnergyHandler` interface with transactional rollback. Shared energy block-entity bases now expose `getEnergyResourceHandler()` while keeping deprecated `getEnergyCapability()` as a downstream transition surface.

STATUS: Removed unused old `FriendlyByteBuf` encode/decode methods from `SyncableFieldPacket`; packet serialization now uses only the registered `StreamCodec`.

STATUS: Restored arbitrary caller-specified block-entity save-data copying in `BaseLootTableGenerator#createStandardTable`. Nucleus now registers `nucleus_pauljoda:copy_block_entity_data`, a 26.1 loot function with a `MAP_CODEC`, through `DeferredRegister` on `Registries.LOOT_FUNCTION_TYPE`; the helper applies it for each `tags...` path and writes matching block-entity save data into the dropped stack's typed `BLOCK_ENTITY_DATA` component.

STATUS: Replaced the remaining compile-preserving `RenderUtils#setColor`, `prepareRenderState`, and `restoreRenderState` GUI no-ops with an explicit 26.1 GUI tint stack. Nucleus GUI texture/text/sprite rendering now routes through `RenderUtils` wrappers that submit the current tint into `GuiGraphicsExtractor` render-state entries.

STATUS: Fixed migrated GUI text color handling where RGB-only values had zero alpha under 26.1 `GuiGraphicsExtractor` and could be skipped. Text widgets and numeric text boxes now normalize RGB colors to opaque ARGB.

STATUS: Replaced the old immediate-mode `GuiHelper#drawIconWithCut` path with a `GuiGraphicsExtractor` partial atlas blit. The helper now requires the extractor because raw tessellator GUI drawing is not a valid 26.1 render-state path.

STATUS: Reduced the fluid GUI widget's deprecated `FluidTank` dependency. `MenuWidgetFluidTank` now has a primary constructor backed by `Supplier<FluidStack>` and `IntSupplier` capacity, and `GuiHelper#renderFluid` has a primary `FluidStack`/capacity overload. The old `FluidTank` constructor/getter/setter and helper overload remain deprecated for downstream source transition only.

STATUS: Fixed GUI render-state stack balance in `MenuBase#drawTopLayer`, `MenuWidgetLongText`, `MenuTab`, and `MenuReverseTab`; old restore calls could pop a caller's state frame under the new explicit tint stack.

BREAKING_CHANGE: `Savable` implementers must now implement `load(ValueInput)` and `save(ValueOutput)`. `load(CompoundTag)` and `save(CompoundTag)` are deprecated compatibility bridges and should not be used as the primary API.

BREAKING_CHANGE: `BaseBlockStateGenerator` no longer extends the removed NeoForge `BlockStateProvider` generator API. Downstream data generators should register it as a `DataProvider` and call `addConnectedTextureModels` before provider execution.

BREAKING_CHANGE: Sided inventory and neighbor energy movement now use NeoForge 26.1 transfer capabilities. Downstream code exposing only old `IItemHandler` / `IEnergyStorage` capabilities should add modern `ResourceHandler<ItemResource>` / `EnergyHandler` registrations.

BREAKING_CHANGE: `EnergyContainingItem` and `InventoryHandlerItem` now expose NeoForge transfer interfaces as their primary runtime API. Existing `IEnergyStorage` / `IItemHandlerModifiable` methods remain as deprecated compatibility surfaces for downstream migration.

BREAKING_CHANGE: Block-entity energy and fluid integrations should register `Capabilities.Energy.BLOCK` with `getEnergyResourceHandler()` and `Capabilities.Fluid.BLOCK` with `getFluidResourceHandler()`. Legacy `IEnergyStorage` / `IFluidHandler` getters are deprecated compatibility surfaces, not the primary 26.1 API.

BREAKING_CHANGE: Item energy/inventory persistence no longer writes Nucleus state into `DataComponents.CUSTOM_DATA`. Downstream code that inspected those tags directly must read `nucleus_pauljoda:item_energy` / `nucleus_pauljoda:item_inventory` data components or use the transfer handlers.

BREAKING_CHANGE: `GuiHelper#drawIconWithCut` now requires a `GuiGraphicsExtractor` argument and records a 26.1 GUI render-state blit instead of drawing through the removed immediate-mode GUI path.

## Changes made

- Switched Gradle plugin from legacy NeoGradle UserDev to `net.neoforged.moddev` `2.0.141`.
- Updated wrapper to Gradle `9.1.0`.
- Installed local JDK 25 at `/home/hermes/.local/jdks/jdk25` because 26.1 requires Java 25.
- Removed Foojay toolchain resolver from `settings.gradle`; resolver `0.10.0` failed under Gradle 9.1 with `JvmVendorSpec.IBM_SEMERU`.
- Updated `gradle.properties`:
  - `minecraft_version=26.1.2`
  - `neo_version=26.1.2.75`
  - `jei_version=29.6.2.31`
  - loader range `[1,)`
- Renamed mod metadata from `META-INF/mods.toml` to `META-INF/neoforge.mods.toml`.
- Updated `pack.mcmeta` resource pack format to `84`.
- Started source migration:
  - `ResourceLocation` -> `Identifier`
  - `GuiGraphics` -> `GuiGraphicsExtractor`
  - Mod bus registration moved to explicit `IEventBus#addListener` for config, networking, and client setup.
  - Networking started moving to `CustomPacketPayload.Type` and `StreamCodec`.
  - Packet sending updated toward new `PacketDistributor` / `ClientPacketDistributor` APIs.
  - `ClickType` -> `ContainerInput` in `BaseContainer`.
  - Direction block properties changed to `EnumProperty<Direction>`.
  - NBT primitive reads updated from `getInt` to `getIntOr` in `EnergyBank`.
  - Item comparison/copy helpers updated to `ItemStack.isSameItemSameComponents` / `copyWithCount`.
  - `TimeUtils` moved to `LevelTickEvent.Post`.
  - `ModelHelper` imports moved to `net.minecraft.client.resources.model.cuboid`.
  - `InventoryContents` bridges `CompoundTag` to `TagValueInput` / `TagValueOutput`.
- Continued source migration to make `compileJava` pass:
  - Kept the custom GUI framework and moved `MenuBase` onto `extractRenderState` / `extractContents`.
  - Adapted GUI mouse and character entry points to `MouseButtonEvent` / `CharacterEvent` while preserving Nucleus widget hooks.
  - Replaced GUI text/item/tooltip calls with `GuiGraphicsExtractor#text`, `#item`, and `#setComponentTooltipForNextFrame`.
  - Replaced old 3D pose-stack GUI operations with 26.1 `Matrix3x2fStack` operations.
  - Replaced `MenuWidgetColoredZone` immediate-mode GL drawing with `GuiGraphicsExtractor#fill`.
  - Updated connected texture `updateShape` signatures and `Direction#getNormal` usages to 26.1 `BlockPos#relative`.
  - Bridged `FluidTank` serialization through `TagValueInput` / `TagValueOutput`.
  - Initially bridged item custom NBT storage to `DataComponents.CUSTOM_DATA` where `ItemStack#getTag` / `setTag` were removed; later replaced Nucleus item energy/inventory persistence with typed data components.
  - Updated wrench block entity stack transfer to `DataComponents.BLOCK_ENTITY_DATA`.
  - Updated recipe helper interfaces to 26.1 `RecipeInput` / `RecipeOutput` signatures.
  - Updated `BaseLootTableGenerator` constructor for the new `VanillaBlockLoot(HolderLookup.Provider)` requirement and container contents to `ContainerComponentManipulators.CONTAINER`.
  - Removed compile-time references to old NeoForge capability constants now replaced by transfer API constants.

## Important caveats

- `BaseBlockStateGenerator` is no longer stubbed, but its 26.1 replacement is a direct JSON `DataProvider` rather than the removed NeoForge client model-generator inheritance tree. Downstream data generators may need source changes if they relied on inherited `BlockStateProvider` helper methods.
- GUI code has only had broad mechanical edits. Minecraft 26.1 changed GUI rendering and input substantially:
  - `AbstractContainerScreen` now uses `extractRenderState` / `extractContents` instead of the old `render` path.
  - Mouse/key input APIs now use event objects.
  - Text, item, and tooltip rendering methods were renamed/reworked.
  - `RenderSystem` state calls and `GlStateManager` usage need removal or replacement.
- STATUS: `GuiHelper#renderFluid` now has a `GuiGraphicsExtractor` implementation. Runtime GUI validation is still needed, especially for custom fluids whose still texture does not follow the conventional `block/<fluid>_still` sprite path.
- STATUS: Item/energy helper methods now use 26.1 `ResourceHandler<ItemResource>` and `transfer.energy.EnergyHandler` for block/item capability lookups. Deprecated direct `IItemHandler` / `IEnergyStorage` signatures still exist where callers pass those objects explicitly.
- STATUS: Core Nucleus block entity persistence has moved to `ValueInput` / `ValueOutput`; old `CompoundTag` bridges remain deprecated for downstream transition.
- STATUS: Loot/data helper `BaseLootTableGenerator` copies block entity custom names, locks, container loot seed data, dynamic container contents, and caller-specified block-entity save-data paths through 26.1 data-component and Nucleus loot functions.

## Verification run history

Succeeded setup steps:

```bash
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" ./gradlew compileJava --stacktrace
```

NeoForm/ModDevGradle successfully downloaded, decompiled, patched, transformed, and recompiled Minecraft `26.1.2` artifacts.

Latest verified command:

```bash
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" ./gradlew compileJava --stacktrace
```

Result on 2026-06-09 UTC:

- STATUS: `BUILD SUCCESSFUL in 14s`
- STATUS: `2 actionable tasks: 1 executed, 1 up-to-date`
- STATUS: javac emitted `100 warnings`, mostly deprecation/removal warnings for old NeoForge `IEnergyStorage`, `IItemHandler`, `IFluidHandler`, `FluidTank`, `SlotItemHandler`, and deprecated screen accessors.
- STATUS: Gradle emitted the Java 25 native-access warning from `native-platform-0.22-milestone-28.jar`.
- STATUS: Gradle emitted a deprecation warning that the build uses features incompatible with Gradle 10.

Latest migration verification on 2026-06-09 UTC:

```bash
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" ./gradlew compileJava --stacktrace
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" ./gradlew build --stacktrace
```

- STATUS: `compileJava` passed: `BUILD SUCCESSFUL in 9s`, `2 actionable tasks: 1 executed, 1 up-to-date`.
- STATUS: `build` passed: `BUILD SUCCESSFUL in 13s`, `8 actionable tasks: 5 executed, 3 up-to-date`.
- STATUS: javac warnings dropped to `14 warnings` on the full build compile pass, all from deprecated/removal NeoForge compatibility interfaces in fluid/energy APIs still exposed by Nucleus.
- STATUS: Javadoc still emits existing documentation warnings, but no longer fails.
- STATUS: Gradle still emits the Java 25 native-access warning and Gradle 10 deprecation warning.

Follow-up verification on 2026-06-09 UTC:

```bash
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" ./gradlew build --stacktrace
```

- STATUS: Initially failed in `:javadoc` because several `MenuBase` Javadoc `@param` names still referenced the old mouse/key method arguments after the 26.1 event-object migration.
- STATUS: Fixed those Javadoc parameter names; rerun result was `BUILD SUCCESSFUL in 13s` with `8 actionable tasks: 6 executed, 2 up-to-date`.
- STATUS: Added `org.gradle.java.installations.paths=/home/hermes/.local/jdks/jdk21,/home/hermes/.local/jdks/jdk25` so Gradle/ModDevGradle can locate both the Java 21 asset-download toolchain and the Java 25 compile/runtime toolchain in this container.

Headless client smoke on 2026-06-09 UTC:

```bash
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" xvfb-run -a timeout 120 ./gradlew runClient --stacktrace
```

- STATUS: Smoke reached NeoForge/Minecraft client startup under Xvfb with llvmpipe GL and loaded `jei`, `minecraft`, `neoforge`, and `nucleus_pauljoda`.
- STATUS: Render thread started (`Setting user: Dev`, `Backend library: LWJGL version 3.4.1+2`) and resource reload began for `vanilla`, `mod_resources`, `mod/nucleus_pauljoda`, `mod/neoforge`, and `mod/jei`.
- STATUS: Command exited with code `124` because the outer `timeout 120` killed the still-running client; this is treated as smoke success per coordinator criteria.
- STATUS: Historical smoke note from before the latest migration pass: obsolete side annotations and pack metadata still produced runtime warnings; those two items were resolved later in this file. Headless ALSA/OpenAL audio errors are container noise and Minecraft disables sound.

Coordinator recheck on 2026-06-09 09:35 UTC:

```bash
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" ./gradlew compileJava --stacktrace
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" ./gradlew build --stacktrace
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" xvfb-run -a timeout 120 ./gradlew runClient --stacktrace
```

- STATUS: `compileJava` recheck passed: `BUILD SUCCESSFUL in 7s`, `2 actionable tasks: 2 up-to-date`.
- STATUS: `build` recheck passed: `BUILD SUCCESSFUL in 7s`, `8 actionable tasks: 8 up-to-date`.
- STATUS: Headless client smoke again reached NeoForge/FML mod discovery and render-thread startup under Xvfb. Mod list included `jei`, `minecraft`, `neoforge`, and `nucleus_pauljoda`; logs reached `Setting user: Dev`, `Backend library: LWJGL version 3.4.1+2`, resource reload, and texture atlas creation.
- STATUS: `runClient` exited code `124` from the intentional `timeout 120`, treated as smoke success per coordinator criteria.
- STATUS: Historical smoke note from before the latest migration pass: obsolete side annotations and pack metadata warnings were still present at this point; those two items were resolved later in this file. Headless ALSA/OpenAL audio disablement is container noise.

Coordinator recheck on 2026-06-09 11:08 UTC:

```bash
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" ./gradlew compileJava --stacktrace
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" ./gradlew build --stacktrace
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" xvfb-run -a timeout 120 ./gradlew runClient --stacktrace
```

- STATUS: `compileJava` recheck passed: `BUILD SUCCESSFUL in 7s`, `2 actionable tasks: 2 up-to-date`.
- STATUS: `build` recheck passed: `BUILD SUCCESSFUL in 7s`, `8 actionable tasks: 8 up-to-date`.
- STATUS: Headless client smoke again reached NeoForge/FML mod discovery and render-thread startup under Xvfb. Mod list included `jei`, `minecraft`, `neoforge`, and `nucleus_pauljoda`; logs reached `Setting user: Dev`, `Backend library: LWJGL version 3.4.1+2`, resource reload, texture atlas creation, and JEI GUI atlas creation.
- STATUS: `runClient` exited code `124` from the intentional `timeout 120`, treated as smoke success per coordinator criteria.
- STATUS: Historical smoke note from before the latest migration pass: obsolete side annotations and pack metadata warnings were still present at this point; those two items were resolved later in this file. Headless ALSA/OpenAL audio disablement is container noise.

Coordinator recheck on 2026-06-09 11:44 UTC:

```bash
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" ./gradlew compileJava --stacktrace
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" ./gradlew build --stacktrace
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" xvfb-run -a timeout 120 ./gradlew runClient --stacktrace
```

- STATUS: `compileJava` recheck passed: `BUILD SUCCESSFUL in 7s`, `2 actionable tasks: 2 up-to-date`.
- STATUS: `build` recheck passed: `BUILD SUCCESSFUL in 7s`, `8 actionable tasks: 8 up-to-date`.
- STATUS: Headless client smoke again reached NeoForge/FML mod discovery and render-thread startup under Xvfb/llvmpipe. Mod list included `jei`, `minecraft`, `neoforge`, and `nucleus_pauljoda`; logs reached `Setting user: Dev`, `Backend library: LWJGL version 3.4.1+2`, resource reload, and texture atlas creation including blocks/items/gui/JEI GUI atlases.
- STATUS: `runClient` exited code `124` from the intentional `timeout 120`, treated as smoke success per coordinator criteria.
- STATUS: Historical smoke note from before the latest migration pass: obsolete side annotations and pack metadata warnings were still present at this point; those two items were resolved later in this file. Headless ALSA/OpenAL audio disablement is container noise.

Coordinator recheck on 2026-06-09 12:19 UTC:

```bash
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" ./gradlew compileJava --stacktrace
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" ./gradlew build --stacktrace
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" xvfb-run -a timeout 120 ./gradlew runClient --stacktrace
```

- STATUS: `compileJava` recheck passed: `BUILD SUCCESSFUL in 7s`, `2 actionable tasks: 2 up-to-date`.
- STATUS: `build` recheck passed: `BUILD SUCCESSFUL in 7s`, `8 actionable tasks: 8 up-to-date`.
- STATUS: Headless client smoke again reached NeoForge/FML mod discovery and render-thread startup under Xvfb/llvmpipe. Mod list included `jei`, `minecraft`, `neoforge`, and `nucleus_pauljoda`; logs reached `Setting user: Dev`, `Backend library: LWJGL version 3.4.1+2`, resource reload, and texture atlas creation including blocks/items/gui/JEI GUI atlases.
- STATUS: `runClient` exited code `124` from the intentional `timeout 120`, treated as smoke success per coordinator criteria.
- STATUS: Historical smoke note from before the latest migration pass: obsolete side annotations and pack metadata warnings were still present at this point; those two items were resolved later in this file. Headless ALSA/OpenAL audio disablement is container noise.

Migration verification on 2026-06-09 14:47 UTC:

```bash
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" ./gradlew compileJava --stacktrace
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" ./gradlew build --stacktrace
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" xvfb-run -a timeout 120 ./gradlew runClient --stacktrace
```

- STATUS: `compileJava` passed: `BUILD SUCCESSFUL in 7s`, `2 actionable tasks: 2 up-to-date`.
- STATUS: `build` passed: `BUILD SUCCESSFUL in 12s`, `8 actionable tasks: 4 executed, 4 up-to-date`.
- STATUS: Headless client smoke reached NeoForge/FML mod discovery and render-thread startup under Xvfb/llvmpipe. Mod list included `jei`, `minecraft`, `neoforge`, and `nucleus_pauljoda`; logs reached `Setting user: Dev`, `Backend library: LWJGL version 3.4.1+2`, resource reload, and texture atlas creation including blocks/items/gui/JEI GUI atlases.
- STATUS: The previous `supported_formats` pack metadata warning is gone after removing deprecated `supported_formats` from `pack.mcmeta`.
- STATUS: `runClient` exited code `124` from the intentional `timeout 120`, treated as smoke success per coordinator criteria. Headless ALSA/OpenAL audio disablement remains container noise.

Migration verification on 2026-06-09 15:13 UTC:

```bash
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" ./gradlew compileJava --stacktrace
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" ./gradlew build --stacktrace
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" xvfb-run -a timeout 120 ./gradlew runClient --stacktrace
```

- STATUS: `compileJava` passed: `BUILD SUCCESSFUL in 9s`, `2 actionable tasks: 1 executed, 1 up-to-date`.
- STATUS: `build` passed: `BUILD SUCCESSFUL in 11s`, `8 actionable tasks: 5 executed, 3 up-to-date`.
- STATUS: `runClient` reached NeoForge/FML mod discovery, loaded `jei`, `minecraft`, `neoforge`, and `nucleus_pauljoda`, reached render-thread startup (`Setting user: Dev`, LWJGL `3.4.1+2`), resource reload, and texture atlas creation including blocks/items/gui/JEI GUI atlases.
- STATUS: `runClient` exited code `124` from the intentional `timeout 120`, treated as smoke success per coordinator criteria. Headless ALSA/OpenAL audio disablement remains container noise.

Migration verification on 2026-06-09 15:44 UTC:

```bash
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" ./gradlew compileJava --stacktrace
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" ./gradlew build --stacktrace
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" xvfb-run -a timeout 120 ./gradlew runClient --stacktrace
```

- STATUS: `compileJava` passed: `BUILD SUCCESSFUL in 9s`, `2 actionable tasks: 1 executed, 1 up-to-date`.
- STATUS: `build` passed: `BUILD SUCCESSFUL in 11s`, `8 actionable tasks: 5 executed, 3 up-to-date`.
- STATUS: `compileJava` emitted `37 warnings`, all from deprecated/removal compatibility surfaces still exposed for downstream transition (`IEnergyStorage`, `IItemHandlerModifiable`, legacy `Savable#load(CompoundTag)` call sites). Fluid deprecation warnings moved to `javadoc`/API surfaces after `compileJava` was re-run from the latest incremental state.
- STATUS: `build` javadoc still emits existing documentation warnings, but does not fail.
- STATUS: `runClient` reached NeoForge/FML mod discovery, loaded `jei`, `minecraft`, `neoforge`, and `nucleus_pauljoda`, reached render-thread startup (`Setting user: Dev`, LWJGL `3.4.1+2`), resource reload, and texture atlas creation including blocks/items/gui/JEI GUI atlases.
- STATUS: `runClient` exited code `124` from the intentional `timeout 120`, treated as smoke success per coordinator criteria. Headless ALSA/OpenAL audio disablement remains container noise.

Migration verification on 2026-06-09 16:32 UTC:

```bash
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" ./gradlew compileJava --stacktrace
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" ./gradlew build --stacktrace
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" xvfb-run -a timeout 120 ./gradlew runClient --stacktrace
```

- STATUS: `compileJava` passed: `BUILD SUCCESSFUL in 9s`, `2 actionable tasks: 1 executed, 1 up-to-date`.
- STATUS: `build` passed: `BUILD SUCCESSFUL in 11s`, `8 actionable tasks: 5 executed, 3 up-to-date`.
- STATUS: `build` javadoc still emits existing documentation warnings, but does not fail.
- STATUS: `runClient` reached NeoForge/FML mod discovery, loaded `jei`, `minecraft`, `neoforge`, and `nucleus_pauljoda`, reached render-thread startup (`Setting user: Dev`, LWJGL `3.4.1+2`), resource reload, and texture atlas creation including blocks/items/gui/JEI GUI atlases.
- STATUS: `runClient` exited code `124` from the intentional `timeout 120`, treated as smoke success per coordinator criteria. Headless ALSA/OpenAL audio disablement remains container noise.

Migration verification on 2026-06-09 16:59 UTC:

```bash
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" ./gradlew compileJava --stacktrace
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" ./gradlew build --stacktrace
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" xvfb-run -a timeout 120 ./gradlew runClient --stacktrace
```

- STATUS: `compileJava` passed: `BUILD SUCCESSFUL in 7s`, `2 actionable tasks: 2 up-to-date`.
- STATUS: `build` initially failed in `:javadoc` because two Javadocs still documented old `FluidTank` parameter names after the supplier-based fluid GUI migration.
- STATUS: Fixed those Javadocs; rerun `build` passed: `BUILD SUCCESSFUL in 13s`, `8 actionable tasks: 6 executed, 2 up-to-date`.
- STATUS: `compileJava` during the successful build emitted `5 warnings`, all from deprecated `FluidTank` transition overloads in `MenuWidgetFluidTank` and `GuiHelper`.
- STATUS: `build` Javadoc still emits existing documentation warnings, but no longer fails.
- STATUS: Headless client smoke reached NeoForge/FML mod discovery, loaded `jei`, `minecraft`, `neoforge`, and `nucleus_pauljoda`, reached render-thread startup (`Setting user: Dev`, LWJGL `3.4.1+2`), resource reload, and texture atlas creation including blocks/items/gui/JEI GUI atlases.
- STATUS: `runClient` exited code `124` from the intentional `timeout 120`, treated as smoke success per coordinator criteria. Headless ALSA/OpenAL audio disablement remains container noise.

Coordinator final verification on 2026-06-09 17:23 UTC:

```bash
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" ./gradlew compileJava --stacktrace
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" ./gradlew build --stacktrace
JAVA_HOME=/home/hermes/.local/jdks/jdk25 PATH="$JAVA_HOME/bin:$PATH" xvfb-run -a timeout 120 ./gradlew runClient --stacktrace
```

- STATUS: `compileJava` passed: `BUILD SUCCESSFUL in 7s`, `2 actionable tasks: 2 up-to-date`.
- STATUS: `build` passed: `BUILD SUCCESSFUL in 7s`, `8 actionable tasks: 8 up-to-date`.
- STATUS: Headless client smoke reached NeoForge/FML mod discovery and render-thread startup under Xvfb/llvmpipe. Mod list included `jei`, `minecraft`, `neoforge`, and `nucleus_pauljoda`; logs reached `Setting user: Dev`, LWJGL `3.4.1+2`, resource reload, and texture atlas creation including blocks/items/gui/JEI GUI atlases.
- STATUS: `runClient` exited code `124` from the intentional `timeout 120`, treated as smoke success per coordinator criteria. Headless ALSA/OpenAL audio disablement remains container noise.

## Areas needing Paul review / incomplete migrations

STATUS: Resolved review item: `BaseBlockStateGenerator` no longer throws `UnsupportedOperationException`; it writes connected texture blockstate/model JSON as a 26.1 `DataProvider`.

STATUS: Resolved review item: `GuiHelper#renderFluid` is no longer a no-op; fluid tank widgets now pass `GuiGraphicsExtractor` and render scaled fluid sprites.

STATUS: Resolved review item: `EnergyUtils` no longer returns zero for neighbor transfer helpers and no longer skips item energy tooltips; it queries 26.1 energy capabilities.

STATUS: Resolved review item: `InventoryUtils` no longer returns `false` for sided block-entity source/target transfers; it uses `ResourceHandler<ItemResource>` where block entities are involved.

STATUS: Resolved review item: `BaseLootTableGenerator` now copies block entity custom names and common components using `CopyComponentsFunction.copyComponentsFromBlockEntity`.

STATUS: Resolved review item: arbitrary caller-specified `tags` in `BaseLootTableGenerator#createStandardTable` are copied from block entity save data by `nucleus_pauljoda:copy_block_entity_data`, a registered Nucleus loot function/provider. The function uses 26.1 `MAP_CODEC` registration and writes matched paths into the dropped stack's typed `BLOCK_ENTITY_DATA` component, preserving the old `BlockEntityTag.<tag>` placement behavior through the modern data component.
STATUS: Resolved review item: `Savable` and the shared block-entity persistence paths now use `ValueInput` / `ValueOutput` as the primary API. Deprecated `CompoundTag` bridges remain only for downstream migration.

STATUS: Resolved review item: `EnergyContainingItem` and `InventoryHandlerItem` now use registered typed Nucleus data components for item energy/inventory, with one-time legacy `CUSTOM_DATA` reads for old stack migration only.

STATUS: Resolved review item: `FluidHandler` and `FluidAndItemHandler` now expose `ResourceHandler<FluidResource>` as their first-class 26.1 fluid transfer API. Deprecated `IFluidHandler` and `FluidTank` surfaces still exist for source transition and because the current storage internals are still backed by NeoForge's deprecated `FluidTank`.

STATUS: Resolved review item: shared block-entity energy bases now expose `getEnergyResourceHandler()` and `EnergyBank` implements transactional `transfer.energy.EnergyHandler`.

STATUS: Resolved review item: `RenderUtils#setColor`, `prepareRenderState`, and `restoreRenderState` are no longer no-op placeholders. They maintain an explicit GUI tint stack, and Nucleus GUI blit/text/sprite callers now submit that tint through 26.1 render-state APIs.

STATUS: Resolved review item: the old immediate-mode `GuiHelper#drawIconWithCut` path no longer builds unsubmitted tessellator vertices. It now requires `GuiGraphicsExtractor` and records a partial atlas blit.

STATUS: Resolved review item: fluid tank GUI rendering no longer requires `FluidTank` as its primary abstraction. `FluidStack` plus explicit capacity is the core render input; `FluidTank` methods remain deprecated source-transition overloads only.

STATUS: Current decision from Paul guidance: `LevelUtils` wrench transfer remains on vanilla `DataComponents.BLOCK_ENTITY_DATA` because block entity custom data is already a typed vanilla item component in 26.1; avoid reintroducing a Nucleus-specific NBT bridge for placed block entity data.

- `src/main/java/com/pauljoda/nucleus/client/gui/MenuBase.java`, `src/main/java/com/pauljoda/nucleus/client/gui/widget/**`, and `src/main/java/com/pauljoda/nucleus/helper/GuiHelper.java`: GUI migration is mostly mechanical to `GuiGraphicsExtractor`, 2D matrices, event objects, and new tooltip/text/item extraction calls.
  - Why it matters: headless smoke only proves startup; custom screens, tabs, text boxes, tooltips, drag/click handling, z/order, nine-patch scaling, and fluid display still need interactive validation even though the render-state no-ops and raw tessellator path have been replaced.
  - QUESTION_FOR_PAUL: Which dependent mod GUI should be used as the acceptance test before treating the GUI framework as migrated?
- `src/main/java/com/pauljoda/nucleus/common/container/BaseContainer.java`: phantom slot handling was mechanically changed from `ClickType` to `ContainerInput`.
  - Why it matters: `ContainerInput` semantics may not map one-for-one to old mouse click/modifier behavior, so phantom slot adjust/fill behavior needs in-game validation.
  - QUESTION_FOR_PAUL: Should phantom slots preserve the exact old click behavior, or align with 26.1 vanilla container input semantics if they differ?
- `src/main/java/com/pauljoda/nucleus/manager/NetworkManager.java`, `src/main/java/com/pauljoda/nucleus/network/PacketManager.java`, and `src/main/java/com/pauljoda/nucleus/network/packets/bidirectional/SyncableFieldPacket.java`: networking now uses `CustomPacketPayload.Type` / `StreamCodec` only, and `sendToAllAround` changed its public signature.
  - Why it matters: sync packets reached compile and startup only; runtime side safety, registration coverage for future packets, client-only `ClientPacketDistributor` calls, and downstream source compatibility need validation.
  - Migration direction: breaking the old `TargetPoint` call shape is acceptable under Paul guidance if the 26.1 API remains correct and documented.

STATUS: Resolved review item: `ConnectedTextureBlock#canConnect` now matches neighboring blocks regardless of emptiness, consistent with `UpdatingConnectedTextureBlock`.

STATUS: Resolved review item: `pack.mcmeta` now includes 26.1 `min_format` and `max_format` without deprecated `supported_formats`; the latest client smoke no longer logs the optional pack metadata warning.

STATUS: Resolved review item: obsolete `@OnlyIn` annotations were removed from tooltip interfaces and `EnergyUtils`.
- `build.gradle` and `gradle.properties`: the port pins Java 25 and local toolchain discovery paths under `/home/hermes/.local/jdks`.
  - Why it matters: this is valid for the audit container but may be too environment-specific for Paul/dependent CI.
  - QUESTION_FOR_PAUL: Should repository defaults require local Java 25 path hints, or should CI/dev docs own JDK installation while Gradle stays machine-neutral?

## Remaining work

1. Keep migrating and runtime-test the custom GUI framework on 26.1; do not remove it unless Paul explicitly decides otherwise.
2. Runtime-test connected textures generated by the new direct JSON `DataProvider` and decide whether the old multi-texture CTM model variants need to be rebuilt rather than the current cube-all connected-state output.
3. Continue removing deprecated public `IItemHandler`, `IFluidHandler`, `IEnergyStorage`, and `FluidTank` compatibility surfaces where downstream breakage is acceptable. Current primary runtime APIs exist for item, energy, fluid transfer, and fluid GUI rendering, but several legacy getters/interfaces remain for source transition.
4. Runtime-test networking registration and side safety for `ClientPacketDistributor` and `SyncableFieldPacket`.
5. Run dependent-mod GUI/capability/world-save smoke tests after downstream mods are updated to the 26.1 APIs.

READY_FOR_REVIEW: `compileJava` and `build` pass on Java 25 after this migration pass. The latest headless `runClient` smoke reached mod loading/render-thread startup under Xvfb before the expected timeout kill.
