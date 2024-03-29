 //控制层 
app.controller('itemCatController' ,function($scope,$controller ,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}


	//保存新建(目前页面所在的parentId作为新建分类的parentId)
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update($scope.entity ); //修改
		}else{
			$scope.entity.parentId=$scope.parentId;//赋予上级ID
			serviceObject=itemCatService.add($scope.entity);//增加
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	//$scope.reloadList();//重新加载
					$scope.findByParentId($scope.parentId);//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框
		if (confirm("是否确定删除该类及其包括的子类？"))
		itemCatService.dele($scope.selectIds).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	$scope.parentId=0;//默认上级id
	//根据上级分类ID查询列表
	$scope.findByParentId=function(parentId){
		$scope.parentId=parentId;//记住上级ID
		itemCatService.findByParentId(parentId).success(
			function(response){
				$scope.list=response;				
			}
		);		
	}
	
	//分类级别
	$scope.grade=1;//默认一级分类
	$scope.setGrade=function(){
		$scope.grade++;//分类级别++
	};
	
	
	$scope.selectList=function(p_entity){
		alert($scope.grade);
		
		if($scope.grade==1){
			//一级分类
			$scope.entity_1=null;
			$scope.entity_2=null;
		}
		if($scope.grade==2){
			$scope.entity_1=p_entity;
			$scope.entity_2=null;
		}
		if($scope.grade==3){

			$scope.entity_2=p_entity;
		}
		
		$scope.findByParentId(p_entity.id);
		
	}

	//定义下拉框数据来源(关联模板列表){data:[{id:35,text:'手机'},{id:37,text:'电视'}]}
	$scope.templateList={data:[]};
	$scope.findTypeTemplateList=function(){
		typeTemplateService.selectTypeTemplateList().success(
			function (response) {
			$scope.templateList={data:response};
		})
	};
	
});	
