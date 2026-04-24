package com.example.netflixtv.data

/**
 * CCTV channel definitions.
 * Each channel has an ID, display title, API parameter for vdn.live.cntv.cn,
 * and optional BUPT university CDN fallback URLs (no DRM).
 */
object ChannelDefinitions {

    data class Channel(
        val id: String,
        val title: String,
        val apiParam: String,
        val fallbackUrls: List<String> = emptyList()
    )

    val ALL: List<Channel> = listOf(
        Channel("cctv1",  "CCTV-1 综合",    "pw://cctv_p2p_hdcctv1",  listOf("http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8")),
        Channel("cctv2",  "CCTV-2 财经",    "pw://cctv_p2p_hdcctv2",  listOf("http://ivi.bupt.edu.cn/hls/cctv2hd.m3u8")),
        Channel("cctv3",  "CCTV-3 综艺",    "pw://cctv_p2p_hdcctv3",  listOf("http://ivi.bupt.edu.cn/hls/cctv3hd.m3u8")),
        Channel("cctv4",  "CCTV-4 中文国际","pw://cctv_p2p_hdcctv4",  listOf("http://ivi.bupt.edu.cn/hls/cctv4.m3u8")),
        Channel("cctv5",  "CCTV-5 体育",    "pw://cctv_p2p_hdcctv5",  listOf("http://ivi.bupt.edu.cn/hls/cctv5hd.m3u8")),
        Channel("cctv5p", "CCTV-5+ 体育",   "pw://cctv_p2p_hdcctv5p", listOf("http://ivi.bupt.edu.cn/hls/cctv5phd.m3u8")),
        Channel("cctv6",  "CCTV-6 电影",    "pw://cctv_p2p_hdcctv6",  listOf("http://ivi.bupt.edu.cn/hls/cctv6hd.m3u8")),
        Channel("cctv7",  "CCTV-7 国防军事","pw://cctv_p2p_hdcctv7",  listOf("http://ivi.bupt.edu.cn/hls/cctv7hd.m3u8")),
        Channel("cctv8",  "CCTV-8 电视剧",  "pw://cctv_p2p_hdcctv8",  listOf("http://ivi.bupt.edu.cn/hls/cctv8hd.m3u8")),
        Channel("cctv9",  "CCTV-9 纪录",    "pw://cctv_p2p_hdcctv9",  listOf("http://ivi.bupt.edu.cn/hls/cctv9.m3u8")),
        Channel("cctv10", "CCTV-10 科教",   "pw://cctv_p2p_hdcctv10", listOf("http://ivi.bupt.edu.cn/hls/cctv10.m3u8")),
        Channel("cctv11", "CCTV-11 戏曲",   "pw://cctv_p2p_hdcctv11", listOf("http://ivi.bupt.edu.cn/hls/cctv11.m3u8")),
        Channel("cctv12", "CCTV-12 社会与法","pw://cctv_p2p_hdcctv12", listOf("http://ivi.bupt.edu.cn/hls/cctv12.m3u8")),
        Channel("cctv13", "CCTV-13 新闻",   "pw://cctv_p2p_hdcctv13", listOf("http://ivi.bupt.edu.cn/hls/cctv13.m3u8")),
        Channel("cctv14", "CCTV-14 少儿",   "pw://cctv_p2p_hdcctv14", listOf("http://ivi.bupt.edu.cn/hls/cctv14.m3u8")),
        Channel("cctv15", "CCTV-15 音乐",   "pw://cctv_p2p_hdcctv15", listOf("http://ivi.bupt.edu.cn/hls/cctv15.m3u8")),
        Channel("cctv16", "CCTV-16 奥林匹克","pw://cctv_p2p_hdcctv16", listOf("http://ivi.bupt.edu.cn/hls/cctv16.m3u8")),
        Channel("cctv17", "CCTV-17 农业农村","pw://cctv_p2p_hdcctv17", listOf("http://ivi.bupt.edu.cn/hls/cctv17.m3u8")),
    )

    /** Map from channel ID -> Channel, for O(1) lookup */
    val BY_ID: Map<String, Channel> = ALL.associateBy { it.id }

    /** Find channel by ID, or null if not found */
    fun find(id: String): Channel? = BY_ID[id]
}
