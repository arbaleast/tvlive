package com.example.netflixtv.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class ContentRepository(val context: Context, private val catalogName: String = "default") {

    private var cachedCategories: List<Category>? = null

    suspend fun loadCategories(): List<Category> {
        cachedCategories?.let { return it }
        return withContext(Dispatchers.IO) {
            cachedCategories?.let { return@withContext it }
            val fallback = buildFallbackCategories()
            val categories = mutableListOf<Category>()
            try {
                val fileName = if (catalogName == "default") "content_data.json" else "content_data_$catalogName.json"
                val jsonText = readAsset(fileName)
                val root = JSONObject(jsonText)
                val categoriesJson = root.optJSONArray("categories") ?: JSONArray()
                for (i in 0 until categoriesJson.length()) {
                    val catObj = categoriesJson.getJSONObject(i)
                    val name = catObj.optString("name")
                    val itemsJson = catObj.optJSONArray("items") ?: JSONArray()
                    val items = mutableListOf<Content>()
                    for (j in 0 until itemsJson.length()) {
                        val itemObj = itemsJson.getJSONObject(j)
                        val content = Content(
                            id = itemObj.optString("id"),
                            title = itemObj.optString("title"),
                            description = itemObj.optString("description"),
                            thumbnailUrl = itemObj.optString("thumbnailUrl"),
                            backdropUrl = itemObj.optString("backdropUrl", itemObj.optString("thumbnailUrl")),
                            videoUrl = itemObj.optString("videoUrl"),
                            category = name,
                            releaseYear = itemObj.optInt("releaseYear"),
                            rating = itemObj.optString("rating"),
                            duration = itemObj.optString("duration")
                        )
                        items.add(content)
                    }
                    categories.add(Category(name, items))
                }
                cachedCategories = if (categories.isNotEmpty()) categories else fallback
            } catch (e: Exception) {
                cachedCategories = fallback
            }
            cachedCategories!!
        }
    }

    suspend fun getAllContent(): List<Content> = loadCategories().flatMap { it.items }

    suspend fun getItemsByCategory(categoryName: String): List<Content> =
        loadCategories().firstOrNull { it.name.equals(categoryName, ignoreCase = true) }?.items ?: emptyList()

    suspend fun getContentById(id: String): Content? = getAllContent().firstOrNull { it.id == id }

    fun getContentByIdSync(id: String): Content? {
        return cachedCategories?.flatMap { it.items }?.firstOrNull { it.id == id }
            ?: getAllContentSync().firstOrNull { it.id == id }
    }

    private fun getAllContentSync(): List<Content> {
        return cachedCategories?.flatMap { it.items } ?: buildFallbackCategories().flatMap { it.items }
    }

    private fun readAsset(fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    private fun buildFallbackCategories(): List<Category> {
        val trending = listOf(
            Content("tr1","The Last Pixel","A data-driven dystopian road movie.",
                "https://via.placeholder.com/300x400?text=The+Last+Pixel",
                "https://via.placeholder.com/1920x1080?text=The+Last+Pixel",
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                "Trending", 2024,"PG-13","1h 58m"),
            Content("tr2","Nebula Nights","Astronauts chase a rumor across a neon-lit galaxy.",
                "https://via.placeholder.com/300x400?text=Nebula+Nights",
                "https://via.placeholder.com/1920x1080?text=Nebula+Nights",
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
                "Trending", 2023,"PG-13","2h 10m"),
            Content("tr3","Orbit Rescue","A team saves civilians in a collapsing space station.",
                "https://via.placeholder.com/300x400?text=Orbit+Rescue",
                "https://via.placeholder.com/1920x1080?text=Orbit+Rescue",
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                "Trending", 2025,"PG-13","2h 5m")
        )
        val action = listOf(
            Content("ac1","Thunder Run","Mad dash through a fortified city.",
                "https://via.placeholder.com/300x400?text=Thunder+Run",
                "https://via.placeholder.com/1920x1080?text=Thunder+Run",
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                "Action", 2022,"R","2h 0m"),
            Content("ac2","Iron Vanguard","Clash of metal and mind in a near-future war.",
                "https://via.placeholder.com/300x400?text=Iron+Vanguard",
                "https://via.placeholder.com/1920x1080?text=Iron+Vanguard",
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                "Action", 2023,"PG-13","2h 15m"),
            Content("ac3","Shadow Siege","A covert unit faces a ruthless cartel.",
                "https://via.placeholder.com/300x400?text=Shadow+Siege",
                "https://via.placeholder.com/1920x1080?text=Shadow+Siege",
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
                "Action", 2021,"R","2h 3m")
        )
        val comedy = listOf(
            Content("co1","Lucky Break","A misfit finally finds his big break.",
                "https://via.placeholder.com/300x400?text=Lucky+Break",
                "https://via.placeholder.com/1920x1080?text=Lucky+Break",
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                "Comedy", 2019,"PG-13","1h 40m"),
            Content("co2","All in Laughs","A group of friends gamble on a life-changing prank.",
                "https://via.placeholder.com/300x400?text=All+in+Laughs",
                "https://via.placeholder.com/1920x1080?text=All+in+Laughs",
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
                "Comedy", 2020,"PG","1h 50m"),
            Content("co3","Saturday Shenanigans","An unruly crew tries to keep a small town entertained.",
                "https://via.placeholder.com/300x400?text=Saturday+Shenanigans",
                "https://via.placeholder.com/1920x1080?text=Saturday+Shenanigans",
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubmarineReturn.mp4",
                "Comedy", 2021,"PG-13","1h 45m")
        )
        val drama = listOf(
            Content("dr1","Echoes of Dawn","Family secrets unravel in a seaside town.",
                "https://via.placeholder.com/300x400?text=Echoes+of+Dawn",
                "https://via.placeholder.com/1920x1080?text=Echoes+of+Dawn",
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                "Drama", 2018,"R","2h 0m"),
            Content("dr2","Velvet Hour","A lawyer confronts a past she thought she left behind.",
                "https://via.placeholder.com/300x400?text=Velvet+Hour",
                "https://via.placeholder.com/1920x1080?text=Velvet+Hour",
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TheGirlWhoLeaptThroughTime.mp4",
                "Drama", 2022,"PG-13","1h 58m"),
            Content("dr3","Broken Borders","A migrant story told through intertwined lives.",
                "https://via.placeholder.com/300x400?text=Broken+Borders",
                "https://via.placeholder.com/1920x1080?text=Broken+Borders",
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/CosmosLaidOver.mp4",
                "Drama", 2024,"R","2h 12m")
        )
        return listOf(
            Category("Trending", trending),
            Category("Action", action),
            Category("Comedy", comedy),
            Category("Drama", drama)
        )
    }

    fun getAvailableCatalogs(): List<String> = listOf("default", "v2")
}