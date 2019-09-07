package com.pinyougou.content.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;
import com.pinyougou.content.service.ContentService;
import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;

	//注入spring-data-jedis核心类redisTemplate
	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		contentMapper.insert(content);
		//新建之后需要从缓存中清空数据，保证缓存数据和数据库数据一致
		redisTemplate.boundHashOps("content").delete(content.getCategoryId());
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		//查询修改前的分类id
		Long oldCategoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
		redisTemplate.boundHashOps("content").delete(oldCategoryId);
		contentMapper.updateByPrimaryKey(content);

		//如果分类ID发生了修改,清除修改后的分类ID的缓存
		if(oldCategoryId.longValue()!=content.getCategoryId().longValue()){
			redisTemplate.boundHashOps("content").delete(content.getCategoryId());
		}

	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
//删除之后需要从缓存中清空数据，保证缓存数据和数据库数据一致
			Long categoryId = contentMapper.selectByPrimaryKey(id).getCategoryId();
			redisTemplate.boundHashOps("content").delete(categoryId);
			contentMapper.deleteByPrimaryKey(id);


		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 从tb_content表的外键（categoryId广告分类id）查询广告列表
	 * @param categoryId
	 * @return
	 */
	@Override
	public List<TbContent> findContentByContentCategoryId(Long categoryId) {
		List<TbContent> list = (List<TbContent>) redisTemplate.boundHashOps("content").get(categoryId);
		if (list==null){
			//从缓存中查找数据，如果为空再从数据库查询
			System.out.println("从数据库查询数据！");
			//查询广告分类列表
			TbContentExample example=new TbContentExample();
			Criteria criteria = example.createCriteria();
			//外键
			criteria.andCategoryIdEqualTo(categoryId);
			//设置状态为启用“1”
			criteria.andStatusEqualTo("1");
			//自然排序
			example.setOrderByClause("sort_order");
			list= contentMapper.selectByExample(example);
			//将数据放入redis缓存中
			redisTemplate.boundHashOps("content").put(categoryId,list);
		}else {
			System.out.println("从缓存中查询数据！");
		}
		return list;
	}

}
