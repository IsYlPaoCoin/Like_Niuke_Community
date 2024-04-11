package com.nowcoder.community.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.community.entity.User;
import com.nowcoder.community.community.service.UserService;
import com.nowcoder.community.community.util.CommunityConstant;
import com.nowcoder.community.community.util.CommunityUtil;
import com.nowcoder.community.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author coolsen
 * @version 1.0.0
 * @ClassName LoginController.java
 * @Description 注册，登录
 * @createTime 4/29/2020 3:13 PM
 */
@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    // 注入value(声明有效路径)
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "/site/register";
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "/site/login";
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功,我们已经向您的邮箱发送了一封激活邮件,请尽快激活!");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功,您的账号已经可以正常使用了!");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作,该账号已经激活过了!");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败,您提供的激活码不正确!");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    //http://localhost:8080/community/kaptcha
    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    //图片需要手动输出，  所以用void
    public void getKaptcha(HttpServletResponse response/*, HttpSession session*/) {
        // 生成验证码
        // 验证码必须存到服务器端   如果存到服务器端容易被盗取
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // v1.将验证码存入session,效率较低
//        session.setAttribute("kaptcha", text);

        // v2.将验证码存入redis，过期时间60s
        //（1）验证码的所属
        // 创建  临时给 客户端 颁发字符串
        String kaptchaOwner = CommunityUtil.generateUUID();
        // 使用cookie  保存  字符串
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);

        //（2）将验证码存入redis
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey, text, 60, TimeUnit.SECONDS);

        // 将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("响应验证码失败:" + e.getMessage());
        }
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberme,
                        Model model,/* HttpSession session,*/ HttpServletResponse response,@CookieValue("kaptchaOwner") String kaptchaOwner) {
        // v1.从session中取值，检查验证码
//        String kaptcha = (String) session.getAttribute("kaptcha");

        // v2.从cookie中取出owner（键），去查redis中的值
        String kaptcha = null;
        // (1) 如果key 不为空， 即数据没有失效的话
        if (StringUtils.isNotBlank(kaptchaOwner)) {
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }
        // 如果取出的验证码为空 || 取出的 code 也为空 || ！kaptcha.equalsIgnoreCase(code)  比较session中的验证码 和 传入的验证码 是否相同
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确!");
            return "/site/login";
        }

        // 2.检查账号,密码
        // 2.1 设置过期时间
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        // 2.2 如果 map 中 包含了ticket ，则表明 成功了
        if (map.containsKey("ticket")) {
            // 实例化cookie 发送给 客户端
            // Cookie 的 key 、value 都必须是 字符串类型
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            // 将 cookie 添加到响应体  ， 进而返回给客户端
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            // 设置错误 返回 消息
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
//        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }
}
//Cat
//02_AmericanBobtail_美国短尾猫
//03_AmericanCurl_美国反耳猫
//04_AmericanShorthair_美国短毛猫
//05_AmericanWirehair_美国刚毛猫
//06_Balinese_巴厘猫
//07_Bengal_孟加拉猫
//08_Birman_伯曼猫
//09_Bombay_孟买猫
//10_BritishShorthair_英国短毛猫
//11_Burmese_缅甸猫
//12_Burmilla_布尔米拉
//13_Chartreux_查特鲁猫
//14_ColorpointShorthair_彩点短毛猫
//15_CornishRex_康沃尔帝王猫
//16_DevonRex_德文雷克斯猫
//17_EgyptianMau_埃及猫
//18_EuropeanBurmese_欧洲缅甸猫
//19_Exotic_异国短毛猫
//20_HavanaBrown_哈瓦那褐色猫（英国种短毛猫）
//21_JapaneseBobtail_日本短尾猫
//22_KhaoManee_泰国白宝石猫
//23_Korat_科拉特猫
//24_LaPerm_拉邦猫
//25_Lykoi_利科伊猫
//26_MaineCoonCat_缅因猫
//27_Manx_马恩岛猫
//28_NorwegianForestCat_挪威森林猫
//29_Ocicat_奥西卡特猫
//30_Oriental_东方猫
//31_Persian_波斯猫
//32_RagaMuffin_拉加曼芬猫
//33_Ragdoll_布偶猫
//34_RussianBlue_俄罗斯蓝猫
//35_ScottishFold_苏格兰折耳猫
//36_SelkirkRex_塞尔凯克卷毛猫
//37_Siamese_暹罗猫
//38_Siberian_西伯利亚猫
//39_Singapura_新加坡猫
//40_Somali_索马里猫
//41_Sphynx_加拿大无毛猫
//42_Tonkinese_东奇尼猫
//43_Toybob_波布猫
//44_TurkishAngora_土耳其安哥拉猫
//45_TurkishVan_土耳其梵猫


//Dog
//01_FrenchBulldog_法国斗牛犬
//02_LabradorRetriever_拉布拉多猎犬
//03_GoldenRetriever_金毛猎犬
//04_GermanShepherd_德国牧羊犬
//05_Poodle_贵妇犬
//06_Bulldog_牛头犬
//07_Rottweiler_罗特韦尔犬
//08_Beagle_比格尔犬
//09_Dachshund_达克斯猎狗
//10_GermanShorthairedPointer_德国短毛指示犬
//11_PembrokeWelshCorgi_彭布洛克威尔士科基犬
//12_AustralianShepherd_澳洲牧羊犬
//13_YorkshireTerrier_约克夏梗犬
//14_CavalierKingCharlesSpaniel_查理士王小猎犬
//15_DobermanPinscher_杜宾犬
//16_Boxer_Boxer犬
//17_MiniatureSchnauzer_迷你雪纳瑞犬
//18_CaneCorso_意大利犬
//19_GreatDane_德国獒犬
//20_ShihTzu_狮子犬
//21_SiberianHusky_西伯利亚哈士奇
//22_BerneseMountainDog_伯尔尼兹山地犬
//23_Pomeranian_波美拉尼亚种犬
//24_BostonTerrier_波士顿犬
//25_Havanese_哈瓦那语犬
//26_EnglishSpringerSpaniel_史宾格犬
//27_ShetlandSheepdog_喜乐蒂牧羊犬
//28_AmericanCockerSpaniel_美国可卡犬
//29_Brittany_布列塔尼犬
//30_BorderCollie_博德牧羊犬
//31_MiniatureAmericanShepherd_迷你美国牧羊犬
//32_BelgianMalinois_比利时玛利诺犬
//33_Vizsla_猎犬
//34_Chihuahua_吉娃娃
//35_Pug_哈巴狗
//36_BassetHound_巴吉度猎犬
//37_Mastiff_大驯犬
//38_Maltese_马耳他犬
//39_Collie_柯利牧羊犬
//40_EnglishCockerSpaniel_英国可卡犬
//41_RhodesianRidgeback_罗得西亚脊背犬
//42_Newfoundland_纽芬兰犬
//43_ShibaInu_柴犬
//44_Weimaraner_德国猎犬
//45_WestHighlandWhiteTerrier_西部高地白挭犬
//46_PortugueseWaterDog_葡萄牙水犬
//47_BichonFrise_比熊犬
//48_AustralianCattleDog_澳大利亚牧牛犬
//49_Dalmatian_斑点犬
//50_Bloodhound_侦探猎犬
//51_Papillon_巴比伦犬
//52_ChesapeakeBayRetriever_乞沙比克猎犬
//53_Samoyed_萨莫耶德犬
//54_Whippet_小灵犬
//55_Akita_秋田犬
//56_StBernard_秋田犬
//57_WirehairedPointingGriffon_刚毛指示格里芬犬
//58_GiantSchnauzer_巨型雪纳瑞犬
//59_GermanWirehairedPointer_德国钢毛指示犬
//60_ScottishTerrier_苏格兰梗犬
//61_Bullmastiff_斗牛獒
//62_CardiganWelshCorgi_卡迪根威尔士柯基犬
//63_ItalianGreyhound_意大利灵缇犬
//64_BullTerrier_斗牛梗犬
//65_Shar-Pei_沙皮犬
//66_AiredaleTerrier_万能梗犬
//67_SoftCoatedWheatenTerrier_爱尔兰软毛挭犬
//68_AlaskanMalamute_阿拉斯加雪橇犬
//69_GreatPyrenees_大白熊犬
//70_CairnTerrier_凯安梗犬
//71_IrishSetter_爱尔兰梗犬
//72_MiniaturePinscher_迷你品钦犬
//73_JackRussellTerrier_杰克罗素梗犬
//74_OldEnglishSheepdog_英国古代牧羊犬
//75_StaffordshireBullTerrier_斯塔福郡斗牛梗犬
//76_LagottoRomagnolo_拉戈多罗马涅洛犬
//77_BiewerTerrier_比厄梗犬
//78_Dogue de Bordeaux_波尔多公爵犬
//79_AnatolianShepherdDog_安纳托利亚牧羊犬
//80_ChineseCrested_中国冠毛犬
//81_NovaScotiaDuckTollingRetriever_新斯科舍省鸭鸣猎犬
//82_BoykinSpaniel_柏金猎犬
//83_GreaterSwissMountainDog_大瑞士山地犬
//84_CotonDeTulear_科顿德图莱尔犬
//85_RatTerrier_鼠梗犬
//86_LhasaApso_拉萨犬
//87_AmericanStaffordshireTerrier_美国史特富郡梗犬
//88_DogoArgentino_阿根廷杜高犬
//89_IrishWolfhound_爱尔兰猎狼犬
//90_Keeshond_荷兰犬
//91_Basenji_巴辛吉犬
//92_ChowChow_松狮犬
//93_EnglishSetter_英国雪达犬
//94_StandardSchnauzer_标准型雪纳瑞犬
//95_BorderTerrier_博德猎狐犬
//96_Pekingese_北京狮子狗
//97_BrusselsGriffon_布鲁塞尔粗毛猎犬
//98_BouvierDesFlandres_佛兰德斯牧羊犬
//99_GordonSetter_戈登雪达犬
//100_NorwegianElkhound_挪威猎鹿犬
//101_Borzoi_俄国狼狗
//102_WireFoxTerrier_硬毛猎狐梗犬
//103_Flat-CoatedRetriever_平滑毛寻回犬
//104_BelgianTervuren_比利时特尔维伦犬
//105_JapaneseChin_日本狮子犬
//106_TibetanTerrier_西藏梗犬
//107_Pointer_指示猎犬
//108_ToyFoxTerrier_玩具狐狸梗
//109_NorwichTerrier_诺里奇梗犬
//110_MiniatureBullTerrier_小型斗牛梗犬
//111_Beauceron_博塞龙犬
//112_SpinoneItaliano_意大利斯皮诺内犬
//113_WelshTerrier_威尔士梗犬
//114_Leonberger_莱昂伯格犬
//115_Schipperke_荷兰船长犬
//116_Xoloitzcuintli_墨西哥无毛犬
//117_Afghan Hound_阿富汗猎狗
//118_Boerboel_布尔犬
//119_NeapolitanMastiff_那不勒斯獒犬
//120_AmericanHairlessTerrier_美国无毛梗犬
//121_ParsonRussellTerrier_帕森罗素梗犬
//122_SilkyTerrier_丝毛梗犬
//123_AmericanEskimoDog_美国爱斯基摩犬
//124_BeardedCollie_古代长须牧羊犬
//125_BelgianSheepdog_比利时牧羊犬
//126_WelshSpringerSpaniel_威尔斯激飞猎犬
//127_TibetanSpaniel_西藏猎犬
//128_BlackRussianTerrier_俄罗斯黑梗
//129_Greyhound_灰犬
//130_ManchesterTerrier_曼彻斯特犬
//131_IrishTerrier_爱尔兰梗犬
//132_IcelandicSheepdog_冰岛牧羊犬
//133_BluetickCoonhound_猎浣熊犬
//134_EnglishToySpaniel_英国玩具猎犬
//135_Saluki_狩猎犬
//136_AustralianTerrier_澳洲梗犬
//137_TibetanMastiff_中亚藏獒
//138_NorfolkTerrier_诺福克梗犬
//139_ClumberSpaniel_黄毛猎犬
//140_Pumi_普米犬
//141_SmoothFoxTerrier_光滑狐梗犬
//142_WirehairedVizsla_有线毛维兹拉犬
//143_LakelandTerrier_湖地犬
//144_RussianToy_俄罗斯玩具犬
//145_GermanPinscher_德国犬
//146_KerryBlueTerrier_凯利蓝梗犬
//147_Barbet_巴贝特犬
//148_Affenpinscher_阿芬平舍尔犬
//149_BlacAndTanCoonhound_黑褐猎浣熊犬
//150_Mudi_木迪犬











