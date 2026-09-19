package com.example.data.model

enum class VideoQuality(
    val label: String,
    val resolutionLabelBn: String,
    val approxBitrateKbps: Int,
    val estimatedMbPer10Min: Double,
    val badge: String
) {
    AUTO(
        label = "Auto (Adaptive)",
        resolutionLabelBn = "অ্যাডাপ্টিভ অটো (নেটওয়ার্ক অনুযায়ী)",
        approxBitrateKbps = 750,
        estimatedMbPer10Min = 6.5,
        badge = "AUTO"
    ),
    AUDIO_ONLY(
        label = "Audio Only (অডিও মোড)",
        resolutionLabelBn = "শুধু অডিও (৯১% এমবি সাশ্রয়)",
        approxBitrateKbps = 64,
        estimatedMbPer10Min = 0.8,
        badge = "AUDIO"
    ),
    P144(
        label = "144p (Ultra Data Saver)",
        resolutionLabelBn = "১৪৪পি (চরম এমবি সাশ্রয় - ৮০%)",
        approxBitrateKbps = 160,
        estimatedMbPer10Min = 1.8,
        badge = "144p"
    ),
    P240(
        label = "240p (Data Saver)",
        resolutionLabelBn = "২৪০পি (ডেটা সাভার - ৬৫%)",
        approxBitrateKbps = 320,
        estimatedMbPer10Min = 3.6,
        badge = "240p"
    ),
    P360(
        label = "360p (Balanced)",
        resolutionLabelBn = "৩৬০পি (সুষম গতি ও কোয়ালিটি)",
        approxBitrateKbps = 650,
        estimatedMbPer10Min = 7.5,
        badge = "360p"
    ),
    P480(
        label = "480p (SD Quality)",
        resolutionLabelBn = "৪৮০পি (এসডি স্মুথ কোয়ালিটি)",
        approxBitrateKbps = 1100,
        estimatedMbPer10Min = 13.0,
        badge = "480p"
    ),
    P720(
        label = "720p (HD Stream)",
        resolutionLabelBn = "৭২০পি এইচডি (হাই ডেফিনিশন)",
        approxBitrateKbps = 2400,
        estimatedMbPer10Min = 28.0,
        badge = "720p"
    ),
    P1080(
        label = "1080p (Full HD)",
        resolutionLabelBn = "১০৮০পি ফুল এইচডি",
        approxBitrateKbps = 4200,
        estimatedMbPer10Min = 50.0,
        badge = "1080p"
    )
}
