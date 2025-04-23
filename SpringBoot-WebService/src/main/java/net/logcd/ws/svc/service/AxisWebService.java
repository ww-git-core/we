package net.logcd.ws.svc.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.stereotype.Service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Lenovo on 2019/8/2.
 */
@Service("AxisWebService")
public class AxisWebService {
    @WebMethod(operationName = "AXISJSON")
    public String AXISJSON(@WebParam(name = "appSerialNumber") String appSerialNumber, @WebParam(name = "utsNodeInfo") String utsNodeInfo) {
        JSONObject result = new JSONObject();
        builJson(utsNodeInfo, result);
        return result.toString();
    }
    private void builJson(@WebParam(name = "utsNodeInfo") String utsNodeInfo, JSONObject result) {
        JSONObject jsonObject = JSONObject.parseObject(utsNodeInfo);
        Object datas = jsonObject.get("datas");
        if (!datas.equals("[]")) {
            JSONArray JSONArray = (JSONArray) jsonObject.get("datas");
            JSONObject message = new JSONObject();
            JSONArray array = new JSONArray();
            for (Object o : JSONArray) {
                JSONObject jsonObject1 = (JSONObject) o;
                JSONObject obj = new JSONObject();
                obj.put("id", jsonObject1.get("id"));
                obj.put("code", "0");
                obj.put("message", jsonObject1.get("success"));
                array.add(obj);
            }
            message.put("datas", array);
            result.put("message", message);
            result.put("status", "0x0000");
            System.out.println(result.toJSONString());
        }
    }

    @WebMethod(operationName = "AXISXML")
    public String AXISXML(@WebParam(name = "appSerialNumber") String appSerialNumber, @WebParam(name = "utsNodeInfo") String utsNodeInfo) throws IOException, DocumentException {
        ByteArrayOutputStream stream = getByteArrayOutputStream(utsNodeInfo);
        return new String(stream.toByteArray(), "UTF-8");
    }
    @WebMethod(operationName = "AXISJSONXML")
    public String AXISJSONXML(@WebParam(name = "appSerialNumber") String appSerialNumber, @WebParam(name = "utsNodeInfo") String utsNodeInfo) throws IOException, DocumentException {
        ByteArrayOutputStream stream = getByteArrayOutputStream(utsNodeInfo);
        String xml = new String(stream.toByteArray(), "UTF-8");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status","0x0000");
        jsonObject.put("message",xml);
        return jsonObject.toString();
    }

    private ByteArrayOutputStream getByteArrayOutputStream(@WebParam(name = "utsNodeInfo") String utsNodeInfo) throws DocumentException, IOException {
        Document document = DocumentHelper.createDocument();
        Element roots = document.addElement("datas");
        document.setRootElement(roots);
        Document document1 = DocumentHelper.parseText(utsNodeInfo);
        List<Element> elements = document1.getRootElement().elements("data");
        for (Element e : elements) {
            Element data = DocumentHelper.createElement("data");
            Element id = e.element("id");
            if (id == null || !id.isTextOnly() || id.getTextTrim().length() == 0) {
                continue;
            }else{
                String textTrim = id.getTextTrim();
                data.addElement("id").setText(textTrim);
            }
            data.addElement("code").setText("0");
            data.addElement("message").setText("success");
            roots.add(data);
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        OutputFormat format = new OutputFormat("\t", true);// 设置缩进为4个空格，并且另起一行为true
        format.setEncoding("UTF-8");
        XMLWriter writer = new XMLWriter(stream, format);
        writer.write(document);
        return stream;
    }

}