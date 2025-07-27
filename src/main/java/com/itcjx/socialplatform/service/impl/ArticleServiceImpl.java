package com.itcjx.socialplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcjx.socialplatform.DTO.ArticleDTO;
import com.itcjx.socialplatform.entity.Article;
import com.itcjx.socialplatform.mapper.ArticleMapper;
import com.itcjx.socialplatform.service.IArticleService;
import com.itcjx.socialplatform.util.JwtTokenUtil;
import com.itcjx.socialplatform.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//@RequiredArgsConstructor
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {

    private static final Logger log = LoggerFactory.getLogger(ArticleServiceImpl.class);

    private ArticleMapper articleMapper;
    private final JwtTokenUtil jwtTokenUtil; // 用于解析Token中的用户ID
    private final ChatClient chatClient;

    public ArticleServiceImpl(ArticleMapper articleMapper, JwtTokenUtil jwtTokenUtil,ChatClient chatClient) {
        this.articleMapper = articleMapper;
        this.jwtTokenUtil = jwtTokenUtil;
        this.chatClient = chatClient;
    }

    // 创建文章
    @Override
    public Result<Long> createArticle(ArticleDTO articleDTO , String token) {
        try{
            Article article = new Article();
            article.setUserId(jwtTokenUtil.getUserIdFromToken(token.trim()));
            article.setTitle(articleDTO.getTitle());
            article.setContent(articleDTO.getContent());
            articleMapper.insert(article);
            return Result.success(article.getId());
        }catch (Exception e){
            log.error("创建文章失败：{}", e.getMessage());
            return Result.error(Result.ErrorCode.ADD_FAILED);
        }
    }

    // 获取文章
    @Override
    public Result<ArticleDTO> getArticleById(Long id) {
        // 查询文章
        Article article = articleMapper.selectById(id);
        if (article == null) {
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }
        ArticleDTO dto = new ArticleDTO();
        dto.setContent(article.getContent());
        dto.setTitle(article.getTitle());
        return Result.success(dto);
    }

    // 删除文章
    @Override
    public Result<Void> deleteArticle(Long ArticleId, String token) {
        //获取token里面的用户id
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        //判断用户id是否一致
        Article article = (Article) articleMapper.selectById(ArticleId);
        if (article == null) {
            return Result.error(Result.ErrorCode.NOT_FOUND,"文章不存在");
        }
        if (!userId.equals(article.getUserId())) {
            return Result.error(Result.ErrorCode.DELETE_FAILED,"无权删除此文章");
        }
        //删除文章
        articleMapper.deleteById(ArticleId);
        return Result.success();
    }

    // 搜索文章(传统+ai)
    @Override
    public Result<List<Article>> searchWithAI(String keyword) {
        if(keyword == null){
            return Result.error(Result.ErrorCode.NOT_FOUND,"请输入搜索关键词");
        }
        // 1. 先用 MyBatis-Plus 进行基础搜索（精确匹配）
        List<Article> crudResults = articleMapper.searchByKeyword(keyword);

        // 2. 调用 AI 生成同义词（扩展搜索范围）
        List<String> keywordList = generateSynonyms(keyword);

        // 3. 用同义词再次查询（使用 MyBatis-Plus 的 IN 查询）
        List<Article> aiResults = articleMapper.searchByKeywords(keywordList);

        // 4. 合并结果并去重
        return Result.success(Stream.concat(crudResults.stream(), aiResults.stream())
                .distinct()
                .collect(Collectors.toList()));
    }
    // 调用ai生成同义词
    private List<String> generateSynonyms(String keyword) {
        // 构造 Prompt（告诉 AI 要生成同义词）
        String prompt = """
            生成与【{{keyword}}】相关的3个同义词，用逗号分隔。
            示例：如果输入“电脑”，输出“笔记本,台式机,主机”""";

        // 调用 AI 并获取结果
        String synonyms = chatClient.prompt()
                .user(u -> u.text(prompt).param("keyword", keyword))
                .call()
                .content();

        // 解析 AI 返回的同义词（字符串 → List）
        return Arrays.stream(synonyms.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
