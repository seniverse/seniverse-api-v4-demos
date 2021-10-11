import 'dart:convert' as convert;
import 'dart:convert';
import 'dart:io';
import 'package:dio/dio.dart';
import 'package:logger/logger.dart';
import 'package:crypto/crypto.dart';

void main(List<String> arguments) async {
  var logger = Logger(printer: PrettyPrinter());
  var dio = Dio();
  try {
    final publicKey = 'xxxx';
    var secretKey = utf8.encode('xxxxxxx');
    var queryMap = {
      'fields': 'precis_minutely',
      'locations': '29.5617:120.0962',
      'public_key': publicKey,
      'ts': '${DateTime
          .now()
          .millisecondsSinceEpoch ~/ 1000}',
      'ttl': '800'
    };
    var stringBuffer = StringBuffer();
    queryMap.forEach((key, value) {
      stringBuffer.write('$key=$value&');
    });
    var queryString = stringBuffer.toString().substring(
        0, stringBuffer.length - 1);
    logger.i(queryString);
    var bytes = utf8.encode(queryString);
    var hmacSha1 = Hmac(sha1, secretKey); // HMAC-SHA256
    var digest = hmacSha1.convert(bytes);
    var dt = convert.base64Encode(digest.bytes);
    queryMap['sig'] = dt;

    var url = 'https://api.seniverse.com/v4';
    var response = await dio.get(url, queryParameters: queryMap);
    if (response.statusCode == 200) {
      logger.i(response.data);
    } else {
      logger.e(response.data);
    }
  } on DioError catch (e) {
    if (e.response != null) {
      logger.e(e.response?.data);
      logger.e(e.response?.extra);
    }
    print(e);
  }
}
