-- ========================================
-- 账号密码管理系统 - 初始化数据
-- 平台类型和平台数据
-- ========================================

USE `account_manage`;

-- ========================================
-- 初始化数据 - 平台类型
-- ========================================
INSERT INTO `sys_platform_type` (`id`, `type_code`, `type_name`, `type_name_en`, `sort_order`, `status`) VALUES
(1, 'SOCIAL', '社交通讯', 'Social & Communication', 1, 1),
(2, 'ECOMMERCE', '电商购物', 'E-Commerce', 2, 1),
(3, 'PAYMENT', '支付金融', 'Payment & Finance', 3, 1),
(4, 'VIDEO', '视频娱乐', 'Video & Entertainment', 4, 1),
(5, 'LIFE', '生活服务', 'Life Services', 5, 1),
(6, 'TRAVEL', '旅游出行', 'Travel & Transportation', 6, 1),
(7, 'WORK', '办公协作', 'Work & Collaboration', 7, 1),
(8, 'TECH', '科技数码', 'Technology & Digital', 8, 1),
(9, 'EDUCATION', '教育学习', 'Education & Learning', 9, 1),
(10, 'NEWS', '新闻资讯', 'News & Information', 10, 1),
(11, 'GAME', '游戏娱乐', 'Gaming', 11, 1),
(12, 'CLOUD', '云服务', 'Cloud Services', 12, 1),
(13, 'DEVELOPER', '开发工具', 'Developer Tools', 13, 1),
(14, 'DESIGN', '设计创作', 'Design & Creative', 14, 1),
(15, 'HEALTH', '健康医疗', 'Health & Medical', 15, 1),
(16, 'REALESTATE', '房产服务', 'Real Estate', 16, 1),
(17, 'RECRUIT', '招聘求职', 'Recruitment', 17, 1),
(18, 'OTHER', '其他', 'Other', 99, 1);

-- ========================================
-- 初始化数据 - 国内平台
-- ========================================
INSERT INTO `sys_platform` (`id`, `platform_code`, `platform_name`, `platform_name_en`, `platform_type_id`, `platform_url`, `country`, `country_name`, `sort_order`, `status`) VALUES
-- 社交通讯
(101, 'WECHAT', '微信', 'WeChat', 1, 'https://weixin.qq.com', 'CN', '中国', 1, 1),
(102, 'QQ', 'QQ', 'QQ', 1, 'https://im.qq.com', 'CN', '中国', 2, 1),
(103, 'WEIBO', '新浪微博', 'Sina Weibo', 1, 'https://weibo.com', 'CN', '中国', 3, 1),
(104, 'DINGTALK', '钉钉', 'DingTalk', 1, 'https://www.dingtalk.com', 'CN', '中国', 4, 1),
(105, 'FEISHU', '飞书', 'Feishu/Lark', 1, 'https://www.feishu.cn', 'CN', '中国', 5, 1),

-- 电商购物
(201, 'TAOBAO', '淘宝', 'Taobao', 2, 'https://www.taobao.com', 'CN', '中国', 1, 1),
(202, 'TMALL', '天猫', 'Tmall', 2, 'https://www.tmall.com', 'CN', '中国', 2, 1),
(203, 'JD', '京东', 'JD.com', 2, 'https://www.jd.com', 'CN', '中国', 3, 1),
(204, 'PDD', '拼多多', 'Pinduoduo', 2, 'https://www.pinduoduo.com', 'CN', '中国', 4, 1),
(205, 'XIANYU', '闲鱼', 'Xianyu', 2, 'https://www.goofish.com', 'CN', '中国', 5, 1),
(206, 'SUNING', '苏宁易购', 'Suning', 2, 'https://www.suning.com', 'CN', '中国', 6, 1),
(207, 'DEWU', '得物', 'Dewu/Poizon', 2, 'https://www.dewu.com', 'CN', '中国', 7, 1),
(208, 'VIPSHOP', '唯品会', 'Vipshop', 2, 'https://www.vip.com', 'CN', '中国', 8, 1),

-- 支付金融
(301, 'ALIPAY', '支付宝', 'Alipay', 3, 'https://www.alipay.com', 'CN', '中国', 1, 1),
(302, 'WECHATPAY', '微信支付', 'WeChat Pay', 3, 'https://pay.weixin.qq.com', 'CN', '中国', 2, 1),
(303, 'UNIONPAY', '云闪付', 'UnionPay', 3, 'https://www.95516.com', 'CN', '中国', 3, 1),
(304, 'CMB', '招商银行', 'China Merchants Bank', 3, 'https://www.cmbchina.com', 'CN', '中国', 4, 1),
(305, 'ICBC', '工商银行', 'ICBC', 3, 'https://www.icbc.com.cn', 'CN', '中国', 5, 1),
(306, 'ABC', '农业银行', 'ABC', 3, 'https://www.abchina.com', 'CN', '中国', 6, 1),
(307, 'BOC', '中国银行', 'Bank of China', 3, 'https://www.boc.cn', 'CN', '中国', 7, 1),
(308, 'CCB', '建设银行', 'CCB', 3, 'https://www.ccb.com', 'CN', '中国', 8, 1),

-- 视频娱乐
(401, 'BILIBILI', '哔哩哔哩', 'Bilibili', 4, 'https://www.bilibili.com', 'CN', '中国', 1, 1),
(402, 'DOUYIN', '抖音', 'Douyin', 4, 'https://www.douyin.com', 'CN', '中国', 2, 1),
(403, 'KUAISHOU', '快手', 'Kuaishou', 4, 'https://www.kuaishou.com', 'CN', '中国', 3, 1),
(404, 'IQIYI', '爱奇艺', 'iQiyi', 4, 'https://www.iqiyi.com', 'CN', '中国', 4, 1),
(405, 'YOUKU', '优酷', 'Youku', 4, 'https://www.youku.com', 'CN', '中国', 5, 1),
(406, 'TENCENT_VIDEO', '腾讯视频', 'Tencent Video', 4, 'https://v.qq.com', 'CN', '中国', 6, 1),
(407, 'MANGO', '芒果TV', 'Mango TV', 4, 'https://www.mgtv.com', 'CN', '中国', 7, 1),
(408, 'XIAOHONGSHU', '小红书', 'Xiaohongshu/RED', 4, 'https://www.xiaohongshu.com', 'CN', '中国', 8, 1),

-- 生活服务
(501, 'MEITUAN', '美团', 'Meituan', 5, 'https://www.meituan.com', 'CN', '中国', 1, 1),
(502, 'ELEME', '饿了么', 'Ele.me', 5, 'https://www.ele.me', 'CN', '中国', 2, 1),
(503, 'DIANPING', '大众点评', 'Dianping', 5, 'https://www.dianping.com', 'CN', '中国', 3, 1),
(504, 'TC58', '58同城', '58.com', 5, 'https://www.58.com', 'CN', '中国', 4, 1),

-- 旅游出行
(601, 'DIDI', '滴滴出行', 'DiDi', 6, 'https://www.didiglobal.com', 'CN', '中国', 1, 1),
(602, 'CTRIP', '携程旅行', 'Ctrip/Trip.com', 6, 'https://www.ctrip.com', 'CN', '中国', 2, 1),
(603, 'QUNAR', '去哪儿', 'Qunar', 6, 'https://www.qunar.com', 'CN', '中国', 3, 1),
(604, 'FLIGGY', '飞猪', 'Fliggy', 6, 'https://www.fliggy.com', 'CN', '中国', 4, 1),
(605, 'GAODE', '高德地图', 'Amap', 6, 'https://www.amap.com', 'CN', '中国', 5, 1),
(606, 'BAIDU_MAP', '百度地图', 'Baidu Map', 6, 'https://map.baidu.com', 'CN', '中国', 6, 1),
(607, 'RAILWAY_12306', '12306', '12306', 6, 'https://www.12306.cn', 'CN', '中国', 7, 1),

-- 科技数码
(701, 'XIAOMI', '小米', 'Xiaomi', 8, 'https://www.mi.com', 'CN', '中国', 1, 1),
(702, 'HUAWEI', '华为', 'Huawei', 8, 'https://www.huawei.com', 'CN', '中国', 2, 1),
(703, 'APPLE_CN', '苹果(中国)', 'Apple China', 8, 'https://www.apple.com.cn', 'CN', '中国', 3, 1),

-- 新闻资讯
(801, 'TOUTIAO', '今日头条', 'Toutiao', 10, 'https://www.toutiao.com', 'CN', '中国', 1, 1),
(802, 'BAIDU', '百度', 'Baidu', 10, 'https://www.baidu.com', 'CN', '中国', 2, 1),
(803, 'ZHIHU', '知乎', 'Zhihu', 10, 'https://www.zhihu.com', 'CN', '中国', 3, 1),

-- 房产服务
(901, 'BEIKE', '贝壳找房', 'Beike', 16, 'https://www.ke.com', 'CN', '中国', 1, 1),
(902, 'LIANJIA', '链家', 'Lianjia', 16, 'https://www.lianjia.com', 'CN', '中国', 2, 1),
(903, 'ANJUKE', '安居客', 'Anjuke', 16, 'https://www.anjuke.com', 'CN', '中国', 3, 1),

-- 开发工具 & 云服务
(1001, 'GITHUB', 'GitHub', 'GitHub', 13, 'https://github.com', 'US', '美国', 1, 1),
(1002, 'GITEE', 'Gitee码云', 'Gitee', 13, 'https://gitee.com', 'CN', '中国', 2, 1),
(1003, 'ALIYUN', '阿里云', 'Alibaba Cloud', 12, 'https://www.aliyun.com', 'CN', '中国', 3, 1),
(1004, 'TENCENT_CLOUD', '腾讯云', 'Tencent Cloud', 12, 'https://cloud.tencent.com', 'CN', '中国', 4, 1),
(1005, 'HUAWEI_CLOUD', '华为云', 'Huawei Cloud', 12, 'https://www.huaweicloud.com', 'CN', '中国', 5, 1);

-- ========================================
-- 初始化数据 - 国外平台
-- ========================================
INSERT INTO `sys_platform` (`id`, `platform_code`, `platform_name`, `platform_name_en`, `platform_type_id`, `platform_url`, `country`, `country_name`, `sort_order`, `status`) VALUES
-- 社交通讯(国外)
(1101, 'FACEBOOK', 'Facebook', 'Facebook', 1, 'https://www.facebook.com', 'US', '美国', 101, 1),
(1102, 'INSTAGRAM', 'Instagram', 'Instagram', 1, 'https://www.instagram.com', 'US', '美国', 102, 1),
(1103, 'TWITTER', 'X(Twitter)', 'X/Twitter', 1, 'https://x.com', 'US', '美国', 103, 1),
(1104, 'LINKEDIN', 'LinkedIn', 'LinkedIn', 1, 'https://www.linkedin.com', 'US', '美国', 104, 1),
(1105, 'TELEGRAM', 'Telegram', 'Telegram', 1, 'https://telegram.org', 'AE', '阿联酋', 105, 1),
(1106, 'DISCORD', 'Discord', 'Discord', 1, 'https://discord.com', 'US', '美国', 106, 1),
(1107, 'WHATSAPP', 'WhatsApp', 'WhatsApp', 1, 'https://www.whatsapp.com', 'US', '美国', 107, 1),
(1108, 'LINE', 'Line', 'Line', 1, 'https://line.me', 'JP', '日本', 108, 1),
(1109, 'SNAPCHAT', 'Snapchat', 'Snapchat', 1, 'https://www.snapchat.com', 'US', '美国', 109, 1),
(1110, 'REDDIT', 'Reddit', 'Reddit', 1, 'https://www.reddit.com', 'US', '美国', 110, 1),

-- 电商购物(国外)
(1201, 'AMAZON', 'Amazon', 'Amazon', 2, 'https://www.amazon.com', 'US', '美国', 101, 1),
(1202, 'EBAY', 'eBay', 'eBay', 2, 'https://www.ebay.com', 'US', '美国', 102, 1),
(1203, 'ALIEXPRESS', '速卖通', 'AliExpress', 2, 'https://www.aliexpress.com', 'CN', '中国', 103, 1),
(1204, 'SHOPEE', 'Shopee', 'Shopee', 2, 'https://shopee.com', 'SG', '新加坡', 104, 1),
(1205, 'LAZADA', 'Lazada', 'Lazada', 2, 'https://www.lazada.com', 'SG', '新加坡', 105, 1),
(1206, 'WISH', 'Wish', 'Wish', 2, 'https://www.wish.com', 'US', '美国', 106, 1),
(1207, 'ETSY', 'Etsy', 'Etsy', 2, 'https://www.etsy.com', 'US', '美国', 107, 1),
(1208, 'WALMART', 'Walmart', 'Walmart', 2, 'https://www.walmart.com', 'US', '美国', 108, 1),

-- 支付金融(国外)
(1301, 'PAYPAL', 'PayPal', 'PayPal', 3, 'https://www.paypal.com', 'US', '美国', 101, 1),
(1302, 'STRIPE', 'Stripe', 'Stripe', 3, 'https://stripe.com', 'US', '美国', 102, 1),
(1303, 'WISE', 'Wise', 'Wise', 3, 'https://wise.com', 'GB', '英国', 103, 1),
(1304, 'REVOLUT', 'Revolut', 'Revolut', 3, 'https://www.revolut.com', 'GB', '英国', 104, 1),

-- 视频娱乐(国外)
(1401, 'YOUTUBE', 'YouTube', 'YouTube', 4, 'https://www.youtube.com', 'US', '美国', 101, 1),
(1402, 'NETFLIX', 'Netflix', 'Netflix', 4, 'https://www.netflix.com', 'US', '美国', 102, 1),
(1403, 'TIKTOK', 'TikTok', 'TikTok', 4, 'https://www.tiktok.com', 'US', '美国', 103, 1),
(1404, 'SPOTIFY', 'Spotify', 'Spotify', 4, 'https://www.spotify.com', 'SE', '瑞典', 104, 1),
(1405, 'TWITCH', 'Twitch', 'Twitch', 4, 'https://www.twitch.tv', 'US', '美国', 105, 1),
(1406, 'DISNEY_PLUS', 'Disney+', 'Disney+', 4, 'https://www.disneyplus.com', 'US', '美国', 106, 1),
(1407, 'HBO_MAX', 'Max(HBO)', 'Max/HBO', 4, 'https://www.max.com', 'US', '美国', 107, 1),
(1408, 'PRIME_VIDEO', 'Prime Video', 'Prime Video', 4, 'https://www.primevideo.com', 'US', '美国', 108, 1),

-- 科技数码(国外)
(1501, 'APPLE', 'Apple', 'Apple', 8, 'https://www.apple.com', 'US', '美国', 101, 1),
(1502, 'GOOGLE', 'Google', 'Google', 8, 'https://www.google.com', 'US', '美国', 102, 1),
(1503, 'MICROSOFT', 'Microsoft', 'Microsoft', 8, 'https://www.microsoft.com', 'US', '美国', 103, 1),
(1504, 'SAMSUNG', 'Samsung', 'Samsung', 8, 'https://www.samsung.com', 'KR', '韩国', 104, 1),
(1505, 'SONY', 'Sony', 'Sony', 8, 'https://www.sony.com', 'JP', '日本', 105, 1),

-- 云服务(国外)
(1601, 'AWS', 'AWS', 'Amazon Web Services', 12, 'https://aws.amazon.com', 'US', '美国', 101, 1),
(1602, 'AZURE', 'Azure', 'Microsoft Azure', 12, 'https://azure.microsoft.com', 'US', '美国', 102, 1),
(1603, 'GCP', 'Google Cloud', 'Google Cloud Platform', 12, 'https://cloud.google.com', 'US', '美国', 103, 1),
(1604, 'DIGITALOCEAN', 'DigitalOcean', 'DigitalOcean', 12, 'https://www.digitalocean.com', 'US', '美国', 104, 1),
(1605, 'CLOUDFLARE', 'Cloudflare', 'Cloudflare', 12, 'https://www.cloudflare.com', 'US', '美国', 105, 1),
(1606, 'VERCEL', 'Vercel', 'Vercel', 12, 'https://vercel.com', 'US', '美国', 106, 1),

-- 开发工具(国外)
(1701, 'GITLAB', 'GitLab', 'GitLab', 13, 'https://gitlab.com', 'US', '美国', 101, 1),
(1702, 'BITBUCKET', 'Bitbucket', 'Bitbucket', 13, 'https://bitbucket.org', 'AU', '澳大利亚', 102, 1),
(1703, 'STACKOVERFLOW', 'Stack Overflow', 'Stack Overflow', 13, 'https://stackoverflow.com', 'US', '美国', 103, 1),
(1704, 'NPM', 'npm', 'npm', 13, 'https://www.npmjs.com', 'US', '美国', 104, 1),
(1705, 'DOCKER_HUB', 'Docker Hub', 'Docker Hub', 13, 'https://hub.docker.com', 'US', '美国', 105, 1),
(1706, 'JETBRAINS', 'JetBrains', 'JetBrains', 13, 'https://www.jetbrains.com', 'CZ', '捷克', 106, 1),

-- 设计创作(国外)
(1801, 'FIGMA', 'Figma', 'Figma', 14, 'https://www.figma.com', 'US', '美国', 101, 1),
(1802, 'CANVA', 'Canva', 'Canva', 14, 'https://www.canva.com', 'AU', '澳大利亚', 102, 1),
(1803, 'ADOBE', 'Adobe', 'Adobe', 14, 'https://www.adobe.com', 'US', '美国', 103, 1),
(1804, 'DRIBBBLE', 'Dribbble', 'Dribbble', 14, 'https://dribbble.com', 'US', '美国', 104, 1),
(1805, 'BEHANCE', 'Behance', 'Behance', 14, 'https://www.behance.net', 'US', '美国', 105, 1),

-- 游戏娱乐(国外)
(1901, 'STEAM', 'Steam', 'Steam', 11, 'https://store.steampowered.com', 'US', '美国', 101, 1),
(1902, 'EPIC', 'Epic Games', 'Epic Games', 11, 'https://www.epicgames.com', 'US', '美国', 102, 1),
(1903, 'PLAYSTATION', 'PlayStation', 'PlayStation', 11, 'https://www.playstation.com', 'JP', '日本', 103, 1),
(1904, 'XBOX', 'Xbox', 'Xbox', 11, 'https://www.xbox.com', 'US', '美国', 104, 1),
(1905, 'NINTENDO', 'Nintendo', 'Nintendo', 11, 'https://www.nintendo.com', 'JP', '日本', 105, 1),
(1906, 'BATTLE_NET', '暴雪战网', 'Battle.net', 11, 'https://www.blizzard.com', 'US', '美国', 106, 1),
(1907, 'EA', 'EA', 'Electronic Arts', 11, 'https://www.ea.com', 'US', '美国', 107, 1),
(1908, 'UBISOFT', 'Ubisoft', 'Ubisoft', 11, 'https://www.ubisoft.com', 'FR', '法国', 108, 1),

-- AI平台
(2001, 'OPENAI', 'OpenAI', 'OpenAI', 13, 'https://openai.com', 'US', '美国', 201, 1),
(2002, 'CHATGPT', 'ChatGPT', 'ChatGPT', 13, 'https://chat.openai.com', 'US', '美国', 202, 1),
(2003, 'CLAUDE', 'Claude', 'Claude', 13, 'https://claude.ai', 'US', '美国', 203, 1),
(2004, 'MIDJOURNEY', 'Midjourney', 'Midjourney', 14, 'https://www.midjourney.com', 'US', '美国', 204, 1),
(2005, 'NOTION', 'Notion', 'Notion', 7, 'https://www.notion.so', 'US', '美国', 205, 1);
