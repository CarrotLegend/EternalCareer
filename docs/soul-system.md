# 镰刀伤害与灵魂套装（永恒职业）

实现项目：EternalCareer，Minecraft 1.20.1 / Forge 47.4.20 / Java 17。
新增属性属于 eternal_career；整合包统一 Tag 和套装配置继续使用 until_eternity。
Goety 本体不需要修改，既有 grave_glove_boost 嵌套关系继续保留。

## 注册与代码入口

所有 Java 路径均相对于 `src/main/java/com/carrot123/eternal_career/`。

| 功能 | 文件 / 入口 |
|---|---|
| 属性注册、添加到玩家 | `registry/ModAttributes.java` 的 `SCYTHE_DAMAGE`、`addPlayerAttributes` |
| 统一物品 Tag | `registry/ModTags.java` 的 `Items.SCYTHES`、`Items.SOUL_ARMOR` |
| 镰刀增伤 | `event/ScytheDamageEvents.java` 的服务端 `LivingHurtEvent` |
| 直接近战检查、奖励公式 | `soul/ScytheCombat.java` |
| 持久灵魂接口和数据 | `career/capability/soul/ISoul.java`、`Soul.java` |
| Capability 注册键和 NBT Provider | `career/capability/soul/SoulCapability.java`、`SoulProvider.java` |
| 附加、Clone、登录、换维度、装备和重载事件 | `event/SoulLifecycleEvents.java` |
| 套装解析、加载、同款检测 | `soul/SoulSetDefinition.java`、`SoulSetReloadListener.java`、`SoulSetManager.java` |
| 获取灵魂、致死复活 | `event/SoulCombatEvents.java` |
| 差量同步队列 | `soul/SoulSyncManager.java` |
| 本模组唯一网络通道、S2C 数据包 | `network/ModNetwork.java`、`SoulSyncPacket.java` |
| 客户端灵魂条 | `client/SoulHudOverlay.java` |

`EternalCareer.java` 在现有初始化流程内注册网络和 Capability。
原有 `TrueChefsKnifeAttackContextMixin` 已适配依赖当前的
`TrueChefsKnifeAbsoluteDamageContext.withAttack`，救赎装备限制仍在方法入口执行。
没有修改被依赖模组的源码或 jar。

## 属性

注册 ID：`eternal_career:scythe_damage`。默认 0，范围 -1 至 1024，可同步。
0.25 表示 +25%，1 表示 +100%，-1 表示 -100%。

```
/attribute @s eternal_career:scythe_damage base set 0.5
```

服务端仅在伤害类型是 `player_attack`、攻击者与直接来源是同一个玩家，
且玩家主手属于 `#until_eternity:scythes` 时乘以 `1 + scythe_damage`。
原有暴击等攻击结算已经提供事件伤害；此倍率之后仍应用护甲等减伤。
普通剑、箭、魔法、宠物伤害不会因为玩家手持镰刀而获得加成。
采用非标准伤害类型或完全绕过 Forge 伤害事件的第三方武器不自动纳入这条路径。

## Soul 数据、同步与复活

服务端为玩家附加 `eternal_career:soul` Capability，NBT 字段为整数 `soul`。
初始为 0，死亡与非死亡 Clone 均复制；退出存档、切换维度不重置。
数据和装备上限独立，任何穿脱装备、换套装或重载都不会裁剪现有余额。

玩家四个护甲槽均非空，均属于 `soul_armor` 且全部属于同一个有效定义的套装 Tag 才激活。
重叠匹配取最高上限，相同时按定义 ID 字典序选择。
未配置、定义格式错误或引用 Tag 不存在时不激活；错误定义记录到日志。
套装 Tag 为空是合法配置，只是不会匹配。

击杀奖励在 `LivingDropsEvent` 结算，该阶段死亡已被接受；是否有掉落物不影响奖励。
只有直接近战镰刀击杀 `Enemy` 敌怪且完整套装有效时才奖励：

```
clamp(floor(目标 getMaxHealth() / 100), 1, 1000)
```

玩家、动物、村民、盔甲架及宠物代杀均不奖励。
正常增加不超过当前上限；已有 Soul 高于上限时保持原值、不再增加。
同一死亡目标不会重复奖励。遵循 Minecraft 的 Enemy 接口；不实现该接口的模组实体不会被猜测为敌怪。

复活在最低优先级 `LivingDeathEvent` 上执行，不接收已取消事件。
原版图腾更早处理；绕过无敌的伤害（例如 /kill）不会触发灵魂复活。
有效套装且有至少 100 Soul 时取消死亡，确认恢复生命后扣除 100，并应用：

- 1 HP；清除旧效果；再生 II 900 tick、伤害吸收 II 100 tick、抗火 I 800 tick。
- `TOTEM_USE` 音效和单独发送的 `TOTEM_OF_UNDYING` 粒子。
- 不发送实体事件 35，不调用中央物品激活动画。

消费仅在服务端发生，重入保护和已处理事件集合避免同次事件重复消费。
不添加额外冷却；以后新的致死伤害仍可触发新的付费复活。
如其他模组在此处理之后强行撤销死亡取消，其行为不在常规 Forge 事件兼容保证内。

S2C 包同步当前值、激活状态、上限。登录、重生、换维度重新发送快照；
余额、护甲或数据包变化加入服务端待处理队列，tick 末尾合并后仅在快照改变时发送。
客户端仅保存显示缓存，退出世界清空，不修改服务端数据。
HUD 随 GUI Scale 使用缩放尺寸定位于右侧中部；无效套装、隐藏 GUI、旁观时隐藏。
槽从下向上填充；`800 / 500` 显示满槽且保留真实数字。

## 配置一个套装

正式资源中的 `soul_armor.json` 默认为空。下面的 examplemod ID 仅为占位，必须替换为已安装物品。
不需要新建 Java 类，不需要重新编译；可以放入世界的 `datapacks/<包名>/`。

### 1. 数据包描述：pack.mcmeta

```json
{
  "pack": {
    "pack_format": 15,
    "description": "Until Eternity soul sets"
  }
}
```

### 2. 镰刀列表：data/until_eternity/tags/items/scythes.json

```json
{
  "replace": false,
  "values": ["examplemod:reaper_scythe"]
}
```

既有 `data/goety/tags/items/grave_glove_boost.json` 已引用此 Tag，无需重复维护。

### 3. 子套装：data/until_eternity/tags/items/soul_sets/reaper.json

```json
{
  "replace": false,
  "values": [
    "examplemod:reaper_helmet",
    "examplemod:reaper_chestplate",
    "examplemod:reaper_leggings",
    "examplemod:reaper_boots"
  ]
}
```

### 4. 总 Tag：data/until_eternity/tags/items/soul_armor.json

```json
{
  "replace": false,
  "values": ["#until_eternity:soul_sets/reaper"]
}
```

### 5. 套装定义：data/until_eternity/soul_sets/reaper.json

```json
{
  "armor_tag": "until_eternity:soul_sets/reaper",
  "max_soul": 500
}
```

`max_soul` 必须是 1 至 2147483647 的整数。
另一个套装建立不同子 Tag 和定义，再把子 Tag 引入总 Tag，即可设置独立上限。
执行 `/reload` 后定义和 Tag 同步更新，已有玩家状态也会重算。
更高优先级的数据包若使用 replace/remove，可改变标签最终成员关系。
只修改开发工程的源码 JSON 不会更新已经安装的 jar，需构建或使用世界数据包。

## 构建和测试

- `gradlew.bat build`：编译、JUnit 数值/NBT 测试、编译独立 GameTest、生产 refmap 验证。
- `gradlew.bat runGameTestServer`：在 `run-soul-tests/` 中启动隔离服务器，加载 `src/soulGameTest` 的测试玩家和套装场景。
- `src/soulGameTest` 中的装备与套装定义仅用于测试，不进入发布 jar。
- 发布包位于 `build/libs/eternal_career-1.0.0.jar`。

实际运行结果见本次交付报告；自动测试不能代替真实客户端对 HUD 位置、声音和粒子的视觉验收。
