function yacysearch(a,b){function h(a,b){var c=a.length;for(var d=0,e=c;d<e;++d){$('<p><img src="/yacy/ui/img-2/cancel_round.png" class="ynav-cancel" /><span class="ytxt"> '+a[d]+"</span></p>").appendTo(b)}$(".ynav-cancel").bind("click",function(b){var c=$("#yquery").getValue();var d=$(b.target).next().text().replace(/^[\s\xA0]+/,"").replace(/[\s\xA0]+$/,"");var e=a.indexOf(d);if(e!=-1)a.splice(e,1);var f=new RegExp(d.replace(/[-[\]{}()*+?.,\\^$|#\s]/g,"\\$&"));$("#yquery").setValue(c.replace(f,"").replace(/^[\s\xA0]+/,"").replace(/[\s\xA0]+$/,""));startRecord=0;$("#yquery").trigger("keyup")})}function g(){window.setTimeout(function(){if($("#yquery").getValue()==ycurr){$("#yside").dialog("open");$("#yquery").focus()}},1e3)}var c=yconf.url+"/yacysearch.json?callback=?";if(b){$("#ypopup").empty();var d="<div class='yloading'><h3 class='linktitle'><em>Loading: "+yconf.url+"</em><br/>";var e="<img src='"+yconf.url+"/yacy/ui/img/loading2.gif' align='absmiddle'/></h3></div>";$("#ypopup").append(d+e);if(!$("#ypopup").dialog("isOpen"))$("#ypopup").dialog("open");else if($("#yside").dialog("isOpen"))$("#yside").dialog("close");$("#yquery").focus()}var f=[];$("#ysearch input").each(function(b){var c={name:$(this).attr("name"),value:$(this).attr("value")};if(c.name=="resource"){if(c.value=="global")a=true;if(a)c.value="global"}if(c.name=="query"||c.name=="search"){if(c.value!=ycurr)ycurr=c.value}f[b]=c});f[f.length]={name:"startRecord",value:startRecord};$.ajaxSetup({timeout:1e4,error:function(){if(b)$("#ypopup").empty();var a="<img src='"+yconf.url+"/yacy/ui/img-2/stop.png' class='favicon'/>";var c="<h3 class='linktitle'>"+a+"Ajax Error!</h3>";var d="<p class='url'><a href=''>Current search terms: "+searchTerms+"</a></p>";var e="<p class='desc'>Sorry, this should not have happened - please try again!</p>";$(c+e+d).appendTo("#ypopup")}});$.getJSON(c,f,function(c,d){if(c[0])data=c[0];else data=c;var e=data.channels[0].searchTerms;if(ycurr.replace(/ /g,"+")!=e){return false}if(b){$("#ypopup").empty()}var f=data.channels[0].totalResults;if(a)var i="global";else var i="local";var j=0;$.each(data.channels[0].items,function(a,b){if(b){var c="<img src='"+yconf.url+"/ViewImage.png?width=16&height=16&code="+b.faviconCode+"' class='favicon'/>";var d="<h3 class='linktitle'>"+c+"<a href='"+b.link+"' target='_blank'>"+b.title+"</a></h3>";var e="<p class='url'><a href='"+b.link+"' target='_blank'>"+b.link+"</a></p>";var f="<p class='desc'>"+b.description+"</p>";var g="<p class='date'>"+b.pubDate.substring(0,16);var h=" | "+b.sizename+"</p>";$(d+f+e+g+h).appendTo("#ypopup")}j++});if(j==0){if(b)$("#ypopup").empty();var k="<img src='"+yconf.url+"/yacy/ui/img-2/stop.png' class='favicon'/>";var l="<h3 class='linktitle'>"+k+"No search results!</h3>";var m="<p class='url'><a href=''>Current search terms: "+e+"</a></p>";var n="<p class='desc'>You could restate your search or release some navigators...</p>";$(l+n+m).appendTo("#ypopup")}if(b){$("#yside").empty();var o=unescape($("#yquery").getValue());var p="local";var q="";var r="";var s="";var t="";if(o.indexOf("/date")!=-1)q='checked="checked"';else r='checked="checked"';if(a){t='checked="checked"';p="global"}else s='checked="checked"';var u="<a href='"+yconf.link+"' target='_blank'><img style='padding-left: 24px;' src='"+yconf.logo+"' alt='"+yconf.logo+"' title='"+yconf.logo+"' /></a>";var v="Total "+p+" results: "+f;$("<div class='ymsg'><table><tr><td width='55px'>"+u+"</td><td id='yresult'>"+v+"</td></tr></div").appendTo("#yside");$("<hr />").appendTo("#yside");$("<p class='ytxt'>You can narrow down your search by selecting one of the below navigators:</p>").appendTo("#yside");$.each(data.channels[0].navigation,function(a,b){if(b){var c="#y"+b.facetname;var d='<label for="y'+b.facetname+'">'+b.displayname+": </label><br />";var e='<select id="y'+b.facetname+'"><option value="">Select one...</option></select>';$('<div class="ui-widget ynav">'+d+e+"</div>").appendTo("#yside");$.each(b.elements,function(a,b){var d='<option value="'+b.modifier+'">'+b.name+" ("+b.count+")</option>";$(d).appendTo(c)});$(c).combobox({selected:function(a,b){var c=unescape($("#yquery").getValue()+" "+b.item.value);$("#yquery").setValue(c);ynavigators.push(b.item.value);$("#yquery").trigger("keyup")}})}});$("<hr />").appendTo("#yside");var w='<table><tr><td><span class="ynav">Get results: </span><div class="yradio" id="yglobal"><input type="radio" id="local" name="yglobal"" '+s+' /><label for="local">local</label><br><input type="radio" id="global" name="yglobal" '+t+' /><label for="global">global</label></div></td>';var x='<td><span class="ynav">Sort by: </span><div class="yradio" id="yrecent"><input type="radio" id="relevance" name="yrecent" '+r+' /><label for="relevance">relevance</label><br><input type="radio" id="date" name="yrecent" '+q+' /><label for="date">date</label></div></td></tr></table>';$(w+x).appendTo("#yside");$("#local, #global, #date, #relevance").change(function(){var b=unescape($("#yquery").getValue());if(this.id=="date"){$("#yquery").setValue(b+" /date")}else if(this.id=="relevance"){$("#yquery").setValue(b.replace(/ \/date/g,""))}else if(this.id=="global"){a=true}else if(this.id=="local"){a=false}$("#yquery").trigger("keyup")});$("<hr />").appendTo("#yside");h(ynavigators,"#yside");if(true){g();if($("#ypopup").dialog("isOpen")){if($("#ypopup h3 :last").position().top<$("#ypopup").dialog("option","height")&&j==maximumRecords){startRecord=startRecord+maximumRecords;yacysearch(submit,false)}}}}})}function yrun(){$.extend($.ui.accordion.defaults,{autoHeight:false,clearStyle:true,collapsible:true,header:"h3"});maximumRecords=parseInt($("#ysearch input[name='maximumRecords']").getValue());$("#ypopup").dialog({autoOpen:false,height:yconf.height,width:yconf.width,minWidth:yconf.width,position:yconf.position,modal:yconf.modal,resizable:yconf.resizable,title:yconf.title,show:yconf.show,hide:yconf.hide,drag:function(a,b){var c=$("#ypopup").parent(".ui-dialog").position();var d=$("#ypopup").parent(".ui-dialog").width()+5+c.left;$("#yside").dialog("option","position",[d,c.top+32])},dragStop:function(a,b){var c=$("#ypopup").parent(".ui-dialog").position();var d=$("#ypopup").parent(".ui-dialog").width()+5+c.left;$("#yside").dialog("option","position",[d,c.top+32])},resizeStop:function(a,b){var c=$("#ypopup").parent(".ui-dialog").position();var d=$("#ypopup").parent(".ui-dialog").height()-55;var e=$("#ypopup").parent(".ui-dialog").width()+5+c.left;$("#yside").dialog("option","height",d);$("#yside").dialog("option","position",[e,c.top+32])},close:function(a,b){$("#yquery").setValue("");$("#yside").dialog("destroy");$("#yside").remove()},open:function(a,b){$('<div id="yside" style="padding:0px;"></div>').insertAfter("#ypopup").parent(".ui-dialog-content");var c=$("#ypopup").parent(".ui-dialog").position();$("#yside").dialog({title:"Navigation",autoOpen:false,draggable:false,resizable:false,width:220,height:$("#ypopup").parent(".ui-dialog").height()-55,minHeight:$("#ypopup").parent(".ui-dialog").height()-55,show:"slide",hide:"slide",position:[c.left+$("#ypopup").parent(".ui-dialog").width()+5,c.top+32],open:function(a,b){$("div.ui-widget-shadow").remove();$("#ypopup").dialog("moveToTop")}});$(".ui-widget-shadow").remove();$('div[aria-labelledby="ui-dialog-title-yside"] div.ui-dialog-titlebar').remove();$("#ypopup").bind("scroll",function(a){p1=$("#ypopup h3 :last").position().top;if(p1-$("#ypopup").dialog("option","height")<0){startRecord=startRecord+maximumRecords;yacysearch(submit,false)}})}});$("#ysearch").keyup(function(a){if(a.which==27){$("#ypopup").dialog("close");$("#yquery").setValue("")}else if(a.which==39){startRecord=startRecord+maximumRecords;yacysearch(submit,false)}if(ycurr==$("#yquery").getValue()){return false}if($("#yquery").getValue()==""){if($("#ypopup").dialog("isOpen"))$("#ypopup").dialog("close")}else{ycurr=$("#yquery").getValue();if(!submit)yacysearch(false,true);else submit=false}return false});$("#ysearch").submit(function(){submit=true;ycurr=$("#yquery").getValue();if(!$("#ypopup").dialog("isOpen"))$("#ypopup").dialog("open");else if($("#yside").dialog("isOpen"))$("#yside").dialog("close");$("#yquery").focus();yacysearch(yconf.global,true);return false})}function statuscheck(){if(load_status<5){return}else{window.clearInterval(loading);yrun()}}$(document).ready(function(){ynavigators=new Array;$.ajaxSetup({timeout:5e3,cache:true});ycurr="";startRecord=0;maximumRecords=10;submit=false;yconf=$.extend({url:"",global:false,theme:"start",title:"YaCy Search Widget",logo:yconf.url+"/yacy/ui/img/yacy-logo.png",link:"http://yacy.net",width:640,height:640,position:[150,50],modal:false,resizable:true,show:"",hide:"",load_js:true,load_css:true},yconf);$('<div id="ypopup" class="classic"></div>').appendTo("#yacylivesearch");if(yconf.load_css){var a=yconf.url+"/portalsearch/yacy-portalsearch.css";var b=yconf.url+"/jquery/themes/"+yconf.theme+"/jquery-ui-1.8.16.custom.css";var c=yconf.url+"/jquery/css/jquery-ui-combobox.css";var d=document.getElementsByTagName("head")[0];$(document.createElement("link")).attr({type:"text/css",href:a,rel:"stylesheet",media:"screen"}).appendTo(d);$(document.createElement("link")).attr({type:"text/css",href:b,rel:"stylesheet",media:"screen"}).appendTo(d);$(document.createElement("link")).attr({type:"text/css",href:c,rel:"stylesheet",media:"screen"}).appendTo(d)}load_status=0;loading=window.setInterval("statuscheck()",200);if(yconf.load_js){var e=yconf.url+"/jquery/js/jquery.query-2.1.7.js";var f=yconf.url+"/jquery/js/jquery.form-2.73.js";var g=yconf.url+"/jquery/js/jquery.field-0.9.2.min.js";var h=yconf.url+"/jquery/js/jquery-ui-1.8.16.custom.min.js";var i=yconf.url+"/jquery/js/jquery-ui-combobox.js";$.getScript(e,function(){load_status++});$.getScript(f,function(){load_status++});$.getScript(g,function(){load_status++});$.getScript(h,function(){load_status++});$.getScript(i,function(){load_status++})}else{yrun()}})