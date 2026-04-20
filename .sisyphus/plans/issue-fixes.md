# 问题修复计划

## 问题清单

### 🚨 高优先级

- [x] 1. **BrowseViewModel catalogId BUG** - catalogId 参数被忽略，目录切换不工作
- [x] 2. **PlayerManager 缺少测试** - 媒体播放核心模块没有单元测试

### ⚠️ 中优先级

- [x] 3. **AppError 未被使用** - sealed class 定义了但 ViewModels 未使用 (保留决策)
- [x] 4. **DpadFocusable 已废弃** - 但仍在 HomeScreen 等处使用
- [x] 5. **AGENTS.md 版本过时** - Compose Compiler 记录为 1.5.4，实际是 1.5.10

### 💡 低优先级

- [x] 6. **SearchViewModel 内存效率** - 将所有内容加载到内存 (保留决策)
- [x] 7. **HomeScreen emoji 无障碍** - 硬编码 emoji 字符

---

## 1. BrowseViewModel catalogId BUG ✅ (已完成)

**问题**: `loadCatalog()` 接收 `catalogId` 参数但完全忽略（因为只有一个 catalog: "default"）

**位置**: `modules/feature-browse/src/main/kotlin/.../BrowseViewModel.kt`

**修复方案**: 简化代码，移除无用的 catalogId 参数

### 详细修复步骤

1. 删除第21行: `private var currentCatalogId: String = repository.catalogId`
2. 修改 init (第27-29行): `loadCatalog()` 无参数
3. 修改 loadCatalog (第31行): `private fun loadCatalog()` 无参数
4. 删除第48行: `currentCatalogId = catalogId`
5. 修改第50行: `currentCatalog = repository.catalogId`
6. 修改 switchCatalog (第66-70行): 直接调用 `loadCatalog()`
7. 修改 refresh (第74-76行): 直接调用 `loadCatalog()`

### 验证命令
```bash
./gradlew :modules:feature-browse:compileDebugKotlin
```

### 状态: 待执行

---

## 2. PlayerManager 测试 ✅ (已完成)

**问题**: 媒体播放核心模块没有单元测试

**位置**: `modules/media/src/main/kotlin/.../PlayerManager.kt`

**修复方案**: 添加 PlayerManagerTest，包含：
- prepare() 测试
- play()/pause() 测试
- 重试逻辑测试
- seekBack/Forward 测试
- release() 测试

---

## 3. AppError 未被使用 ✅ (已完成 - 保留决策)

**问题**: AppError sealed class 定义了但 ViewModels 使用 String

**位置**: `modules/data/src/main/kotlin/.../AppError.kt`

**修复方案**: 选择 A) 在 ViewModels 中使用 AppError，或 B) 删除未使用的 AppError

---

## 4. DpadFocusable 已废弃 ✅ (已完成)

**问题**: DpadFocusable 已标记废弃但仍在使用

**位置**: `modules/feature-home/src/main/kotlin/.../HomeScreen.kt:128-154`

**修复方案**: 替换为原生 `.focusable()` modifier

---

## 5. AGENTS.md 版本过时 ✅ (已完成)

**问题**: Compose Compiler 记录为 1.5.4，实际是 1.5.10

**位置**: `AGENTS.md:64`

**修复方案**: 更新版本号

---

## 6. SearchViewModel 内存效率 ✅ (已完成 - 保留决策)

**问题**: 将所有内容加载到内存进行搜索

**位置**: `modules/feature-search/src/main/kotlin/.../SearchViewModel.kt:36-49`

**修复方案**: 考虑懒加载或过滤已有内容

---

## 7. HomeScreen emoji 无障碍 ✅ (已完成)

**问题**: 硬编码 emoji 字符 (🔍 ☰)

**位置**: `modules/feature-home/src/main/kotlin/.../HomeScreen.kt:135,149`

**修复方案**: 使用 Compose Icon 组件