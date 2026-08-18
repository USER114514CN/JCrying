package com.user114514.encryptor.extend_pack;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.user114514.encryptor.excep.IllegalManifestException;

public class PackageManifest implements Serializable {
    private static final long serialVersionUID = 1L;

    public String packName ;
    public String packId;
    public String packPublisher = "Unknown";
    public int integerVersion;
    public String versionName;
    public int minRuntimeVersion = 1;
    public String type = "ALP";
    public Map<String, String> subAttirbutes = new HashMap<>();

    public static PackageManifest loadBy(InputStream is) throws Exception {
        PackageManifest instance = new PackageManifest();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(is);
        Element root = doc.getDocumentElement();
        if (!verify(root, "manifest")) throw new IllegalManifestException("清单 XML 文件的根标签必须为 <manifest>。");
        long frameworkVersion;
        String syntaxVersionAttriuteValue = root.getAttribute("syntaxVersion");
        try {
            frameworkVersion = Long.parseLong(syntaxVersionAttriuteValue);
        } catch (NumberFormatException ex) {
            throw new IllegalManifestException("无效的 syntaxVersion 属性(为空或非数字格式): " + syntaxVersionAttriuteValue);
        }
        if (frameworkVersion > serialVersionUID) throw new IllegalManifestException("此 XML 清单文件的格式语法与当前框架并不匹配: (CurrentFrameworkVersion=" + serialVersionUID + ", PackageAdaptedFrameworkVersion=" + syntaxVersionAttriuteValue + ")");
        NodeList rootNodeList = root.getChildNodes();
        Element packElement = findFristTag(rootNodeList, "package");
        if (packElement == null) throw new IllegalManifestException("找不到 <manifest> 中的 <package> 属性。");
        String packId = packElement.getAttribute("packId");
        if (packId.isEmpty()) throw new IllegalManifestException("属性缺失或为空: packId。");
        instance.packId = packId;
        String intVerString = packElement.getAttribute("integerVersion");
        int integerVer;
        try {
            integerVer = Integer.parseInt(intVerString);
        } catch (NumberFormatException ex) {
            throw new IllegalManifestException("无效的 integerVersion 属性(为空或非数字格式): " + intVerString);
        }
        instance.integerVersion = integerVer;

        Element attrsElement = findFristTag(packElement.getChildNodes(), "pack-attributes");
        if (attrsElement == null) {
            instance.packName = packId;
            instance.versionName = Integer.toString(instance.integerVersion);
            return instance;
        }
        instance.packName = getAttributeOrDefault(attrsElement, "packName", packId);
        instance.packPublisher = getAttributeOrDefault(attrsElement, "packPublisher", "Unknown");
        instance.versionName = getAttributeOrDefault(attrsElement, "versionName", Integer.toString(instance.integerVersion));
        instance.type = getAttributeOrDefault(attrsElement, "packType", "ALP");
        String minRuntimeVerString = getAttributeOrDefault(attrsElement, "minRuntimeVersion", "1");
        try {
            instance.minRuntimeVersion = Integer.parseInt(minRuntimeVerString);
        } catch (NumberFormatException ex) {
            System.out.println("警告: <pack-attributes> 标签的minRuntimeVersion 并不是合法的数字, 视为 1: " + minRuntimeVerString);
        }

        instance.type = getAttributeOrDefault(attrsElement, "packType", "ALP");
        String typeFullyName = ExtendPackageManager.typeFullyName(instance.type);
        if (typeFullyName == null) throw new IllegalManifestException("未知的包类型：" + instance.type);
        Element subAttrElement = findFristTag(attrsElement.getChildNodes(), typeFullyName + "-attributes");
        if (subAttrElement != null) {
            instance.subAttirbutes.putAll(attributesToMap(subAttrElement.getAttributes()));
        }
        return instance;
    }

    public static PackageManifest loadBy(String xmlStr) throws Exception {
        PackageManifest instance = new PackageManifest();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(xmlStr.getBytes(StandardCharsets.UTF_8))) {
            Document doc = builder.parse(bais);
            Element root = doc.getDocumentElement();
            if (!verify(root, "manifest")) throw new IllegalManifestException("清单 XML 文件的根标签必须为 <manifest>。");
            long frameworkVersion;
            String syntaxVersionAttriuteValue = root.getAttribute("syntaxVersion");
            try {
                frameworkVersion = Long.parseLong(syntaxVersionAttriuteValue);
            } catch (NumberFormatException ex) {
                throw new IllegalManifestException("无效的 syntaxVersion 属性(为空或非数字格式): " + syntaxVersionAttriuteValue);
            }
            if (frameworkVersion > serialVersionUID) throw new IllegalManifestException("此 XML 清单文件的格式语法与当前框架并不匹配: (CurrentSyntaxVersion=" + serialVersionUID + ", PackageSyntaxVersion=" + syntaxVersionAttriuteValue + ")");
            NodeList rootNodeList = root.getChildNodes();
            Element packElement = findFristTag(rootNodeList, "package");
            if (packElement == null) throw new IllegalManifestException("找不到 <manifest> 中的 <package> 属性。");
            String packId = packElement.getAttribute("packId");
            if (packId.isEmpty()) throw new IllegalManifestException("属性缺失或为空: packId。");
            instance.packId = packId;
            String intVerString = packElement.getAttribute("integerVersion");
            int integerVer;
            try {
                integerVer = Integer.parseInt(intVerString);
            } catch (NumberFormatException ex) {
                throw new IllegalManifestException("无效的 integerVersion 属性(为空或非数字格式): " + intVerString);
            }
            instance.integerVersion = integerVer;

            Element attrsElement = findFristTag(packElement.getChildNodes(), "pack-attributes");
            if (attrsElement == null) {
                instance.packName = packId;
                instance.versionName = Integer.toString(instance.integerVersion);
                return instance;
            }
            instance.packName = getAttributeOrDefault(attrsElement, "packName", packId);
            instance.packPublisher = getAttributeOrDefault(attrsElement, "packPublisher", "Unknown");
            instance.versionName = getAttributeOrDefault(attrsElement, "versionName", Integer.toString(instance.integerVersion));
            instance.type = getAttributeOrDefault(attrsElement, "packType", "ALP");
            String minRuntimeVerString = getAttributeOrDefault(attrsElement, "minRuntimeVersion", "1");
            try {
                instance.minRuntimeVersion = Integer.parseInt(minRuntimeVerString);
            } catch (NumberFormatException ex) {
                System.out.println("警告: <pack-attributes> 标签的minRuntimeVersion 并不是合法的数字, 视为 1: " + minRuntimeVerString);
            }

            instance.type = getAttributeOrDefault(attrsElement, "packType", "ALP");
            String typeFullyName = ExtendPackageManager.typeFullyName(instance.type);
            if (typeFullyName == null) throw new IllegalManifestException("未知的包类型：" + instance.type);
            Element subAttrElement = findFristTag(attrsElement.getChildNodes(), typeFullyName + "-attributes");
            if (subAttrElement != null) {
                instance.subAttirbutes.putAll(attributesToMap(subAttrElement.getAttributes()));
            }
            return instance;
        }
    }

    public static PackageManifest loadBy2(String xmlStr) throws Exception {
        PackageManifest instance = new PackageManifest();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlStr);
        Element root = doc.getDocumentElement();
        if (!verify(root, "manifest")) throw new IllegalManifestException("清单 XML 文件的根标签必须为 <manifest>。");
        long frameworkVersion;
        String syntaxVersionAttriuteValue = root.getAttribute("syntaxVersion");
        try {
            frameworkVersion = Long.parseLong(syntaxVersionAttriuteValue);
        } catch (NumberFormatException ex) {
            throw new IllegalManifestException("无效的 syntaxVersion 属性(为空或非数字格式): " + syntaxVersionAttriuteValue);
        }
        if (frameworkVersion > serialVersionUID) throw new IllegalManifestException("此 XML 清单文件的格式语法与当前框架并不匹配: (CurrentSyntaxVersion=" + serialVersionUID + ", PackageSyntaxVersion=" + syntaxVersionAttriuteValue + ")");
        NodeList rootNodeList = root.getChildNodes();
        Element packElement = findFristTag(rootNodeList, "package");
        if (packElement == null) throw new IllegalManifestException("找不到 <manifest> 中的 <package> 属性。");
        String packId = packElement.getAttribute("packId");
        if (packId.isEmpty()) throw new IllegalManifestException("属性缺失或为空: packId。");
        instance.packId = packId;
        String intVerString = packElement.getAttribute("integerVersion");
        int integerVer;
        try {
            integerVer = Integer.parseInt(intVerString);
        } catch (NumberFormatException ex) {
            throw new IllegalManifestException("无效的 integerVersion 属性(为空或非数字格式): " + intVerString);
        }
        instance.integerVersion = integerVer;

        Element attrsElement = findFristTag(packElement.getChildNodes(), "pack-attributes");
        if (attrsElement == null) {
            instance.packName = packId;
            instance.versionName = Integer.toString(instance.integerVersion);
            return instance;
        }
        instance.packName = getAttributeOrDefault(attrsElement, "packName", packId);
        instance.packPublisher = getAttributeOrDefault(attrsElement, "packPublisher", "Unknown");
        instance.versionName = getAttributeOrDefault(attrsElement, "versionName", Integer.toString(instance.integerVersion));
        instance.type = getAttributeOrDefault(attrsElement, "packType", "ALP");
        String minRuntimeVerString = getAttributeOrDefault(attrsElement, "minRuntimeVersion", "1");
        try {
            instance.minRuntimeVersion = Integer.parseInt(minRuntimeVerString);
        } catch (NumberFormatException ex) {
            System.out.println("警告: <pack-attributes> 标签的minRuntimeVersion 并不是合法的数字, 视为 1: " + minRuntimeVerString);
        }

        instance.type = getAttributeOrDefault(attrsElement, "packType", "ALP");
        String typeFullyName = ExtendPackageManager.typeFullyName(instance.type);
        if (typeFullyName == null) throw new IllegalManifestException("未知的包类型：" + instance.type);
        Element subAttrElement = findFristTag(attrsElement.getChildNodes(), typeFullyName + "-attributes");
        if (subAttrElement != null) {
            instance.subAttirbutes.putAll(attributesToMap(subAttrElement.getAttributes()));
        }
        return instance;
    }

    private static boolean verify(Element el, String tagName) {
        return el.getTagName().toLowerCase().equals(tagName);
    }

    private static Element findFristTag(NodeList list, String tagName) {
        for (int i = 0; i < list.getLength(); i++) {
            Node current = list.item(i);
            if (current.getNodeType() == Node.ELEMENT_NODE && verify((Element)current, tagName)) return (Element) current;
        }
        return null;
    }

    private static String getAttributeOrDefault(Element el, String s, String def) {
        return (el.hasAttribute(s) ? el.getAttribute(s) : def);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("包清单信息：\n");
        sb.append("  包ID: ").append(packId).append("\n");
        sb.append("  包名称: ").append(packName).append("\n");
        sb.append("  包发布者: ").append(packPublisher).append("\n");
        sb.append("  整数版本号: ").append(integerVersion).append("\n");
        sb.append("  版本名称: ").append(versionName).append("\n");
        sb.append("  最低运行时版本: ").append(minRuntimeVersion).append("\n");
        sb.append("  包类型: ").append(type).append("\n");
        sb.append("  子属性: ");
        if (subAttirbutes == null || subAttirbutes.isEmpty()) {
            sb.append("无");
        } else {
            sb.append("\n");
            for (Map.Entry<String, String> entry : subAttirbutes.entrySet()) {
                sb.append("    ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

     public static Map<String, String> attributesToMap(NamedNodeMap attrs) {
        Map<String, String> map = new HashMap<>();
        if (attrs == null) {
            return map;
        }
        int len = attrs.getLength();
        for (int i = 0; i < len; i++) {
            Attr attr = (Attr) attrs.item(i);
            map.put(attr.getName(), attr.getValue());
        }
        return map;
    }
}
