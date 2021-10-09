import 'dart:convert' as convert;
import 'dart:convert';
import 'dart:io';
import 'package:dio/dio.dart';
import 'package:logger/logger.dart';
import 'package:crypto/crypto.dart';

void main(List<String> arguments) async {
  var logger = Logger(printer: PrettyPrinter());
   var dio =Dio();
  try{
    final  publicKey ='xxxx';
    var secretKey  = utf8.encode('xxxxxxx');

    var queryMap = {'fields':'precis_minutely','locations':'29.5617:120.0962','public_key':publicKey,'ts':'${DateTime.now().millisecondsSinceEpoch~/1000}','ttl':'800'};

    var stringBuffer=StringBuffer();

    queryMap.forEach((key, value) {
      stringBuffer.write('$key=$value&');
    });
    var queryString =stringBuffer.toString().substring(0,stringBuffer.length-1);
    logger.i(queryString);
    var bytes = utf8.encode(queryString);
    var hmacSha1 = Hmac(sha1, secretKey ); // HMAC-SHA256
    var digest = hmacSha1.convert(bytes);
   var dt =  convert.base64Encode(digest.bytes);
   queryMap['sig']= dt;


// Await the http get response, then decode the json-formatted response.
//     logger.i(dt);
//     logger.i(urldt);

  var url='https://api.seniverse.com/v4';
  // var queryMap = {'fields':'precis_minutely','locations':'29.5617:120.0962','public_key':publicKey,'ts':'${DateTime.now().millisecond}','ttl':'800'};
  // var queryMap = {'fields':'precis_minutely','locations':'29.5617:120.0962','public_key':publicKey,'ts':'${DateTime.now().millisecond}','ttl':'800','sig':''};
    dio.interceptors.add(InterceptorsWrapper(
        onRequest:(options, handler){
          // Do something before request is sent

          logger.i(options.queryParameters);
          return handler.next(options); //continue
          // 如果你想完成请求并返回一些自定义数据，你可以resolve一个Response对象 `handler.resolve(response)`。
          // 这样请求将会被终止，上层then会被调用，then中返回的数据将是你的自定义response.
          //
          // 如果你想终止请求并触发一个错误,你可以返回一个`DioError`对象,如`handler.reject(error)`，
          // 这样请求将被中止并触发异常，上层catchError会被调用。
        },
        onResponse:(response,handler) {
          // Do something with response data
          return handler.next(response); // continue
          // 如果你想终止请求并触发一个错误,你可以 reject 一个`DioError`对象,如`handler.reject(error)`，
          // 这样请求将被中止并触发异常，上层catchError会被调用。
        },
        onError: (DioError e, handler) {
          // Do something with response error
          return  handler.next(e);//continue
          // 如果你想完成请求并返回一些自定义数据，可以resolve 一个`Response`,如`handler.resolve(response)`。
          // 这样请求将会被终止，上层then会被调用，then中返回的数据将是你的自定义response.
        }
    ));
  var response = await dio.get(url,queryParameters: queryMap);


//   var jsonResponse =
//   convert.jsonDecode(response.body) as Map<String, dynamic>;
//   var itemCount = jsonResponse['totalItems'];
//   print('Number of books about http: $itemCount.');
// } else {
//   print('Request failed with status: ${response.statusCode}.');
// }
  if(response.statusCode==200){
    // print(response.headers);
    // print(response.data);
    logger.i(response.data);

  }else{
    logger.e(response.data);
  }


  }on DioError catch(e){
    if(e.response != null){
      logger.e(e.response?.data);
      logger.e(e.response?.extra);
    }
    print(e);
  }
  print('Hello world!');
}
