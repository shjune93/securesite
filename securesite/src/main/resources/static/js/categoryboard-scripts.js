

$(".answer-write input[type=submit]").click(addAnswer);

$(".link-delete-article").click(deleteAnswer);


function addAnswer(e){
	e.preventDefault();
	console.log("click me");
	
	var queryString=$(".answer-write").serialize();
	console.log("query :" + queryString);

	var url=$(".answer-write").attr("action");
	console.log("url : " + url)
	$.ajax({
			type:'post',
			url:url,
			data:queryString,
			dataType:'json', 
			success:onSuccess,
			error:function(request,status,error){
				console.log("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
			   }
			});
}

function onError(){
	
}


function onSuccess(data,status){
	console.log(data);
	
	

	var answerTemplate=$("#answerTemplate").html();
	var template=answerTemplate.format( //필요한데이터 추출
			data.writer.userId,
			data.formattedCreateDate,
			data.contents,
//			data.freeboard.id,
			data.id
			);
	$(".qna-comment-slipp-articles").prepend(template); //데이터 순서에 맞게 채우기
//	
	$("textarea[name=contents]").val(""); //입력후 폼비우기
}
function deleteAnswer(e){
	e.preventDefault();
	var deleteBtn=$(this);
	var url=$(this).attr("href");
	console.log("url : " +url);
	
	$.ajax({
		type:'post',
		url:url,
		dataType:'json',
		error:function(xhr,status){
			console.log("error");
		},
		success:function(data,status){
			console.log(data);
			if(data.valid){
				deleteBtn.closest("article").remove();
			}else{
				alert(data.errorMessage);
			}
			
		}
	});
}

String.prototype.format = function() {
	  var args = arguments;
	  return this.replace(/{(\d+)}/g, function(match, number) {
	    return typeof args[number] != 'undefined'
	        ? args[number]
	        : match
	        ;
	  });
	};
