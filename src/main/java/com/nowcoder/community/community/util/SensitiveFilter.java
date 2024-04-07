package com.nowcoder.community.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: SensitiveFilter
 * Package: com.nowcoder.community.community.util
 * Description:
 *
 * @Author 杨理
 * @Create 2024/3/31 12:13
 * @Version 1.0
 */
@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符
    private static final String REPLACEMENT = "***";

    // 根节点
    private TrieNode rootNode = new TrieNode();

    // 容器实例 初始化后  ，  这个方法 会被自动调用
    @PostConstruct
    public void init(){
        try(
            // 读取文件 中的 值  => 从类加载器 中获取
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            // 1. 将字节流（读取不太方便） 转换为 字符流
            // new InputStreamReader(is);
            // 2. 将字符流  转换成 缓冲流
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            ) {
            // 每次 读取的值 都存在 keyword中
            String keyword;
            while((keyword = reader.readLine()) != null){
                // 读取到了的话 将敏感词 添加到前缀树
                this.addKeyword(keyword);
            }


        }catch (IOException e){
            logger.error("加载敏感词文件失败:" + e.getMessage());
        }

    }

    // 定义 前缀树 结点
    private class TrieNode {
        // 关键词 结束 标识
        private boolean isKeywordEnd = false;

        // 子节点 =》(key 是 下级字符，value 是 下级节点) 通过 当前 节点 找到其 子节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        // 添加子节点
        public void addSubNode(Character c,TrieNode node){
            subNodes.put(c,node);
        }

        // 获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }

    /**
     *  过滤敏感词
     * @param text  待过滤的文本
     * @return  过滤后的文本
     */
    public String filter(String text) {
        if(StringUtils.isBlank(text)){
            return null;
        }

        // 指针1 指向的是  树
        TrieNode tempNode = rootNode;
        // 指针2 指向的是 字符串的首位
        int begin = 0;
        // 指针3 指向的是 字符串的首位
        int position = 0;
        // 还要有  一个变量  记录 最终结果  [通过一个变长 字符串 来记录]
        StringBuilder sb =new StringBuilder();

        // 指针3 如果没到结尾  则需要继续  遍历
        while (position < text.length()){

            // 得到当前 位置的 某一个字符
            char c = text.charAt(position);

            // 跳过 符号
            if(isSymbol(c)){
                // 是特殊符号
                // 指针1 处于 根节点 ，将此 符号 计入结果， 让指针2 向下走一步
                if(tempNode == rootNode){
                    sb.append(c);
                    begin++;
                }
                // 无论 符号 在开头或中间 ， 指针3 都向下 走一步
                position++;
                continue;
            }

            // 检查 下层 节点
            tempNode = tempNode.getSubNode(c);
            if(tempNode == null){
                // 以 begin 开头的字符串 不是敏感词
                sb.append(text.charAt(begin));
                // 进入 下一个位置
                position = ++begin;
                // 重新 指向 根节点
                tempNode = rootNode;
            }else if(tempNode.isKeywordEnd()){
                // 发现了 敏感词
                // 将begin~position 字符串 替换掉
                sb.append(REPLACEMENT);
                // 进入下一个位置
                begin = ++position;
                // 重新指向 根节点
                tempNode = rootNode;
            } else {
                // 检查 下一个 字符
                position++;
            }
        }
        // 将最后一批 字符计入 结果
        sb.append(text.substring(begin));

        return sb.toString();
    }

    // 判断 是否为 符号
    private boolean isSymbol(Character c){
        // 判断是否是 特殊 字符
        // 0x2E80 ~ 0x9FFF 东亚的文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }


    // 将一个 敏感词 添加到 前缀树中
    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for(int i =0; i<keyword.length(); i++){
            // 获取 第i个 位置的字符
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);

            if(subNode == null){
                // 初始化 子节点
                subNode = new TrieNode();
                // 把新建的 子节点 挂到 当前节点上
                tempNode.addSubNode(c, subNode);
            }

            // 指向子节点  ，  进入下一轮循环
            tempNode = subNode;

            // 设置 结束 标识
            if (i == keyword.length() -1){
                tempNode.setKeywordEnd(true);
            }
        }
    }
}





















