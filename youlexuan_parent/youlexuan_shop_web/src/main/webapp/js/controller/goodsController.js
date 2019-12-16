//goods控制层 
app.controller('goodsController' ,function($scope, $controller, $location, goodsService, uploadService, itemCatService, typeTemplateService){
	
	// 继承
	$controller("baseController", {
		$scope : $scope
	});

	// 判断修改之后再修改
	$scope.changeSpec = 0;

	// 保存
	$scope.save = function() {

		$scope.changeSpec = 0;

		$scope.entity.goodsDesc.introduction = editor.html();

		goodsService.save($scope.entity).success(function(response) {
			if (response.success) {
				alert("保存成功");
				$scope.entity = {goods:{},goodsDesc:{itemImages:[], specificationItems: []}};
				editor.html('');// 清空富文本编辑器
			} else {
				alert(response.message);
			}
		});
	}
	
	//查询实体 
	$scope.findOne = function(){

		// 获取html传来的参数
		var id = $location.search()["id"];

		// 如果为空, 则为新增, 直接return
		if (id == null) {
			return;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;

				// 赋值到富文本编辑器
				editor.html($scope.entity.goodsDesc.introduction);

				// 转换成json对象
				$scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
				$scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
				$scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
				for (var i = 0; i < $scope.entity.itemList.length; i++) {
					$scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
				}
			}
		);
	}
	
	//批量删除 
	$scope.dele = function(){			
		//获取选中的复选框			
		goodsService.dele($scope.selectIds).success(
			function(response){
				if(response.success){
					$scope.reloadList();
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	// 定义搜索对象 
	$scope.searchEntity = {auditStatus:'0'};
	// 搜索
	$scope.search = function(page,size){			
		goodsService.search(page,size,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;
			}			
		);
	}


	// 上传图片
	$scope.uploadFile = function () {
		uploadService.uploadFile().success(function (resp) {
			if (resp) {
				$scope.image_entity.url = resp.message;
			}else {
				alert(resp.message);
			}
		}).error(function () {
			alert("上传发生错误")
		})
	}

	$scope.entity = {goods:{},goodsDesc:{itemImages:[], specificationItems: []}};//定义页面实体结构
	// 添加图片
	$scope.saveImage = function () {
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	}
	// 删除图片
	$scope.delImageEntity = function (index) {
		$scope.entity.goodsDesc.itemImages.splice(index, 1);
	}

	
	// 一级下拉菜单
	$scope.selectItemCat1List = function () {
		itemCatService.findByParentId(0).success(function (resp) {
			$scope.itemCat1List = resp;

		})
	}

	// 二级下拉菜单
	$scope.$watch('entity.goods.category1Id', function (newVal, oldVal) {
		if (newVal) {
			itemCatService.findByParentId(newVal).success(function (resp) {
				$scope.itemCat2List = resp;

				// 清空三级菜单
				$scope.itemCat3List = [];

				// 清空模板id
				$scope.entity.goods.typeTemplateId = null;


			})
		}
	})

	// 三级级下拉菜单
	$scope.$watch('entity.goods.category2Id', function (newVal, oldVal) {
		if (newVal) {
			itemCatService.findByParentId(newVal).success(function (resp) {
				$scope.itemCat3List = resp;

				// 清空模板id
				$scope.entity.goods.typeTemplateId = null;


			})
		}
	})

	// 模板id
	$scope.$watch('entity.goods.category3Id', function (newVal, oldVal) {
		if (newVal) {
			itemCatService.findOne(newVal).success(function (resp) {
				$scope.entity.goods.typeTemplateId = resp.typeId;


			})
		}
	})

	// 品牌下拉列表
	$scope.brandList = {data:[]};
	$scope.$watch('entity.goods.typeTemplateId', function (newVal, oldVal) {

		// 清规格
		if ($location.search()['id'] == null) {
			// 新增
			$scope.entity.goodsDesc.specificationItems = [];
			$scope.entity.itemList = [];
		}
		if (newVal) {

			typeTemplateService.findOne(newVal).success(function (resp) {
				// 获得模板
				$scope.typeTemplate = resp;
				// 将品牌列表转为json对象
				$scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
				// 只有当id为null时运行, 即只有新增时才运行
				if ($location.search()['id'] == null) {
					// 将扩展列表转为json对象
					$scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
				}
				// 将规格列表转换为json对象
				// $scope.typeTemplate.specIds = JSON.parse($scope.typeTemplate.specIds);
			})

			typeTemplateService.findSpecList($scope.entity.goods.typeTemplateId).success(function (resp) {
				$scope.specList=resp;
			})
		}
	})
	
	// 更新选中的规格选项
	$scope.updateSpecOption = function ($event, specName, optionName) {
		var obj = $scope.findObjByKey($scope.entity.goodsDesc.specificationItems, "attributeName", specName);
		// 选中
		if ($event.target.checked) {
			if (obj != null) {
				// 添加attributeName级
				obj.attributeValue.push(optionName);
			} else {
				// 添加specificationItems部
				$scope.entity.goodsDesc.specificationItems.push({"attributeName":specName,"attributeValue":[optionName]});
			}
		} else {
			// 移除选中的规格选项
			obj.attributeValue.splice(obj.attributeValue.indexOf(optionName), 1);
			// 判断是否移除完一条规格参数
			if (obj.attributeValue.length == 0) {
				$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(obj), 1)
			}
		}


	}

	// 根据选中的规格选项,动态创建item行
	$scope.creatItem = function () {
		$scope.entity.itemList = [{spec:{},price:0,num:999,status:'1',isDefault:'0'}];
		for (var i = 0; i < $scope.entity.goodsDesc.specificationItems.length; i++) {
			$scope.entity.itemList = addRow($scope.entity.itemList, $scope.entity.goodsDesc.specificationItems[i].attributeName, $scope.entity.goodsDesc.specificationItems[i].attributeValue);
		}
	}
	
	addRow = function (list, columnName, conlumnValues) {
		var newList = [];
		for (var i = 0; i < list.length; i++) {
			var oldRow = list[i];
			for (var j = 0; j < conlumnValues.length; j++) {
				var newRow = JSON.parse(JSON.stringify(oldRow));
				newRow.spec[columnName] = conlumnValues[j];
				newList.push(newRow);
			}
		}
		return newList;
	}

	// 商品状态
	$scope.goodsStatus = ['未审核','已审核','审核未通过','关闭'];


	// 级别名称
	$scope.itemCatList = [];
	$scope.findItemCatList = function () {
		itemCatService.findItemCatList().success(function (resp) {
			for (var i = 0; i < resp.length; i++) {
				$scope.itemCatList[resp[i].id] = resp[i].name;
			}
		})
	}

	// 将规格选项选中
	$scope.isItemSelected = function (attrName, attrVal) {
		// 遍历查找规格选项
		for (var i = 0; i < $scope.entity.goodsDesc.specificationItems.length; i++) {
			if ($scope.entity.goodsDesc.specificationItems[i].attributeName == attrName) {
				for (var j = 0; j < $scope.entity.goodsDesc.specificationItems[i].attributeValue.length; j++) {
					if ($scope.entity.goodsDesc.specificationItems[i].attributeValue[j] == attrVal) {
						return true;
					}
				}
			} 
		}
		return false;
	}

	$scope.updateAuditState = function (status) {
		goodsService.updateAuditState($scope.selectIds, status).success(function (resp) {
			if (resp.success) {
				$scope.reloadList();
				$scope.selectIds=[];
			}
		})
	}
    
});	
