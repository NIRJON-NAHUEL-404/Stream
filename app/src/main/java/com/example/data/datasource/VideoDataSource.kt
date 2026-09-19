package com.example.data.datasource

import com.example.R
import com.example.data.model.Comment
import com.example.data.model.VideoItem

object VideoDataSource {
    val categories = listOf(
        "সকল",
        "প্রযুক্তি",
        "গান ও সুর",
        "প্রকৃতি ও ভ্রমণ",
        "টিউটোরিয়াল",
        "পডকাস্ট",
        "অ্যানিমেশন"
    )

    val sampleVideos = listOf(
        VideoItem(
            id = "vid_1",
            title = "স্মার্টফোনে ইন্টারনেট এমবি ও ব্যাটারি বাঁচানোর সেরা ৫টি গোপন ট্রিকস",
            channelName = "Tech Bangla Pro",
            channelAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&q=80",
            subscribers = "১.২ মিলিয়ন",
            views = "৪৫০ হাজার ভিউ",
            uploadDate = "২ দিন আগে",
            durationText = "09:56",
            durationSeconds = 596,
            description = "ইউটিউব এবং ইন্টারনেট ব্রাউজ করার সময় কীভাবে ৯০% পর্যন্ত মোবাইল ডেটা ও ব্যাটারি বাঁচাবেন তা এই ভিডিওতে বিস্তারিত দেখানো হয়েছে। লো কোয়ালিটি রেজোলিউশন ও অডিও-অনলি স্ট্রিমিং এর সঠিক ব্যবহার শিখুন।",
            category = "প্রযুক্তি",
            streamUrl = "android.resource://com.aistudio.litetube.vstrx/${R.raw.sample_video}",
            localThumbnailRes = R.drawable.img_thumb_tech,
            likesCount = 24500,
            tags = listOf("DataSaver", "TechBangla", "TipsAndTricks", "Android"),
            commentsCount = 384
        ),
        VideoItem(
            id = "vid_2",
            title = "হিমালয়ের রহস্যময় পাহাড়ি জলপ্রপাত ও শান্ত সবুজ বনাঞ্চল - ৪K ভিজ্যুয়াল জার্নি",
            channelName = "Nature Explorer BD",
            channelAvatar = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=100&q=80",
            subscribers = "৮৫০ হাজার",
            views = "১.১ মিলিয়ন ভিউ",
            uploadDate = "৫ দিন আগে",
            durationText = "12:15",
            durationSeconds = 735,
            description = "প্রকৃতির অপরূপ সৌন্দর্য, কলকল জলপ্রপাতের শব্দ এবং পাখির ডাক। মানসিক প্রশান্তির জন্য এই ভিডিওটি অত্যন্ত মনোরম। লো-এমবি মোডেও খুব স্মুথ ভাবে উপভোগ করা যাবে।",
            category = "প্রকৃতি ও ভ্রমণ",
            streamUrl = "https://samplelib.com/mp4/sample-30s.mp4",
            localThumbnailRes = R.drawable.img_thumb_nature,
            likesCount = 89000,
            tags = listOf("Nature", "Relaxing", "Waterfall", "Travel"),
            commentsCount = 512
        ),
        VideoItem(
            id = "vid_3",
            title = "বৃষ্টি ভেজা সন্ধ্যায় অ্যাকোস্টিক গিটার ও বাঁশির মিষ্টি সুর - স্টাডি ও রিল্যাক্সিং মিউজিক",
            channelName = "Acoustic Melody Lab",
            channelAvatar = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=100&q=80",
            subscribers = "৩.৪ মিলিয়ন",
            views = "২.৮ মিলিয়ন ভিউ",
            uploadDate = "১ সপ্তাহ আগে",
            durationText = "15:40",
            durationSeconds = 940,
            description = "মন শান্ত করার মতো স্নিগ্ধ ইন্সট্রুমেন্টাল সুর। অডিও মোডে মাত্র কয়েক এমবি খরচে পুরো ট্র্যাকটি শুনতে পারবেন ব্যাকগ্রাউন্ডে।",
            category = "গান ও সুর",
            streamUrl = "android.resource://com.aistudio.litetube.vstrx/${R.raw.sample_audio}",
            localThumbnailRes = R.drawable.img_thumb_tech,
            likesCount = 142000,
            tags = listOf("Music", "Acoustic", "Instrumental", "Focus"),
            commentsCount = 920
        ),
        VideoItem(
            id = "vid_4",
            title = "বিগ বাক বানি - হাই কোয়ালিটি থ্রিডি অ্যানিমেশন অ্যাডভেঞ্চার",
            channelName = "Blender Open Studio",
            channelAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&q=80",
            subscribers = "৫.১ মিলিয়ন",
            views = "১৫ মিলিয়ন ভিউ",
            uploadDate = "৩ সপ্তাহ আগে",
            durationText = "09:56",
            durationSeconds = 596,
            description = "আন্তর্জাতিক জনপ্রিয় ওপেন সোর্স শর্ট অ্যানিমেশন ফিল্ম 'Big Buck Bunny'। দুর্দান্ত কালার গ্রেডিং ও স্মুথ ফ্রেমরেট।",
            category = "অ্যানিমেশন",
            streamUrl = "https://samplelib.com/mp4/sample-20s.mp4",
            localThumbnailRes = R.drawable.img_thumb_nature,
            likesCount = 310000,
            tags = listOf("Blender", "Animation", "3D", "Story"),
            commentsCount = 1240
        ),
        VideoItem(
            id = "vid_5",
            title = "টিয়ার্স অব স্টিল - ভবিষ্যৎ পৃথিবীর বৈজ্ঞানিক কল্পকাহিনী ভিজ্যুয়াল মাস্টারপিস",
            channelName = "SciFi World Studios",
            channelAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100&q=80",
            subscribers = "৯২০ হাজার",
            views = "৯৫০ হাজার ভিউ",
            uploadDate = "১ মাস আগে",
            durationText = "12:14",
            durationSeconds = 734,
            description = "ভবিষ্যতের আমস্টারডামে ধারণকৃত দুর্দান্ত রোবট ও মানুষের সাই-ফাই অ্যাকশন। অ্যাডাপ্টিভ রেজোলিউশনে যেকোনো ব্যান্ডউইথেই বাফারিং ছাড়া চলে।",
            category = "প্রযুক্তি",
            streamUrl = "https://raw.githubusercontent.com/mediaelement/mediaelement-files/master/echo-hereweare.mp4",
            localThumbnailRes = R.drawable.img_thumb_tech,
            likesCount = 57000,
            tags = listOf("SciFi", "VFX", "Action", "Future"),
            commentsCount = 410
        ),
        VideoItem(
            id = "vid_6",
            title = "শূন্য থেকে এআই ও আধুনিক অ্যাপ ডেভেলপমেন্ট শেখার সহজ গাইডলাইন ২০২৬",
            channelName = "Code with Tanvir",
            channelAvatar = "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=100&q=80",
            subscribers = "৬৪০ হাজার",
            views = "৩১০ হাজার ভিউ",
            uploadDate = "৩ দিন আগে",
            durationText = "18:22",
            durationSeconds = 1102,
            description = "প্রোগ্রামিং শেখার শুরু থেকে প্রফেশনাল লেভেলে পৌঁছানোর রোডম্যাপ। ডেটা সাশ্রয় করে শেখার জন্য ১৪৪পি বা ৩৬০পিতে খুব স্পষ্টভাবে টেক্সট দেখা যায়।",
            category = "টিউটোরিয়াল",
            streamUrl = "https://samplelib.com/mp4/sample-10s.mp4",
            localThumbnailRes = R.drawable.img_thumb_tech,
            likesCount = 43000,
            tags = listOf("Coding", "Android", "AI", "Learning"),
            commentsCount = 670
        )
    )

    fun getSampleComments(videoId: String): List<Comment> {
        return listOf(
            Comment(
                id = "c1",
                author = "রাকিবুল হাসান",
                text = "খুব দ্রুত ভিডিও লোড হচ্ছে! ইউটিউবের চেয়ে অনেক কম এমবি খরচ হচ্ছে ভাই। দারুণ ফিচার!",
                timeAgo = "১ ঘন্টা আগে",
                likes = 45,
                isLiked = true
            ),
            Comment(
                id = "c2",
                author = "সাদিয়া ইসলাম",
                text = "অডিও-অনলি মোডটা অসাধারণ! গান আর পডকাস্ট শুনার জন্য একদম পারফেক্ট। ফোনের ডেটা আর শেষ হবে না।",
                timeAgo = "৩ ঘন্টা আগে",
                likes = 29
            ),
            Comment(
                id = "c3",
                author = "তানিম আহমেদ",
                text = "ভিডিও ডাউনলোড করে অফলাইনে দেখতে পাচ্ছি কোনো নেট কানেকশন ছাড়াই। প্লেয়ারটা অনেক স্মুথ।",
                timeAgo = "৫ ঘন্টা আগে",
                likes = 18
            ),
            Comment(
                id = "c4",
                author = "নুসরাত জাহান",
                text = "১৪৪পি ও ২৪০পি রেজোলিউশনেও সুন্দর ক্লিয়ার বোঝা যাচ্ছে। আমার মতো যাদের সীমিত ডেটা তাদের জন্য সেরা প্ল্যাটফর্ম!",
                timeAgo = "১ দিন আগে",
                likes = 63
            )
        )
    }
}
