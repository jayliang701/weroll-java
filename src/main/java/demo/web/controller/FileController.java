package demo.web.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

class WechatAuthResponse {
    public int errcode;

    public String errmsg;

    public HashMap<String, ?> hints;
}

@Controller
@RequestMapping("/file")
public class FileController {

    @GetMapping("wxauth")
    public String getOpenID(@RequestParam("code") String code) {
        final String appid = "wxb0a3902d9f28f042";
        final String secret = "e291ac8584b6daefd7a1ba95e22678b8";
        final String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appid + "&secret=" + secret + "&js_code=" + code + "&grant_type=authorization_code";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        JSONObject result = (JSONObject) JSONObject.parse(response.getBody());

        System.out.println("openid -----> " + result.get("openid"));

        return result.toString();
    }

    @ResponseBody
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) throws FileNotFoundException {
        if (file.isEmpty()) {
            return "false";
        }
        // 获取文件名
        String fileName = file.getOriginalFilename();
        System.out.println("上传的文件名为:" + fileName);
        // 获取文件的后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        System.out.println("上传的后缀名为:" + suffixName);

        // 生成随机文件名
        fileName = UUID.randomUUID() + suffixName;
        System.out.println("上传后随机文件名:" + suffixName);

        //获取跟目录
        File path = new File(ResourceUtils.getURL("classpath:").getPath());
        if(!path.exists()) path = new File("");
        System.out.println("项目根目录: "+ path.getAbsolutePath());

        // 文件上传后最终存储的目录
        //如果上传目录为/static/images/upload/，则可以如下获取：
        File dest = new File(path.getAbsolutePath(), "static/upload/" + fileName);


        // 检测是否存在目录
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            file.transferTo(dest);
            return "true";
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "false";
    }
}
