package com.jimi.utils;

import com.jimi.exception.LgPlatBusinessException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * poi操作excel表，暂时只支持2003版excel导入导出
 * Created by jimi on 15-10-15.
 */
public class PoiExcelUtil {

    protected Log log = LogFactory.getLog(this.getClass());

    private POIFSFileSystem fs; //文件
    private HSSFWorkbook wb; //工作簿
    private HSSFSheet sheet; //工作表
    private HSSFRow row; //行


    /**
     * 根据数据生成excel
     * @param headers
     * @param fields
     * @param dataList
     * @param out
     * @param <T>
     */
    public <T> void writeExcel(String[] headers, String[] fields, List<T> dataList, OutputStream out){
        // 声明一个工作薄
        wb = new HSSFWorkbook();
        // 生成一个表格
        HSSFSheet sheet = wb.createSheet("结果");

        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth((short) 15);
        // 生成一个样式
        HSSFCellStyle headerStyle = wb.createCellStyle();
        // 设置这些样式
        headerStyle.setFillForegroundColor(HSSFColor.TEAL.index);
        headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        HSSFFont font = wb.createFont();
        font.setColor(HSSFColor.WHITE.index);
        font.setFontHeightInPoints((short) 10);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        headerStyle.setFont(font);

        // 生成并设置另一个样式
        HSSFCellStyle style2 = wb.createCellStyle();
        style2.setFillForegroundColor(HSSFColor.WHITE.index);
        style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 生成另一个字体
        HSSFFont font2 = wb.createFont();
        font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style2.setFont(font2);

        // 声明一个画图的顶级管理器
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
        // 定义注释的大小和位置,详见文档
        HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0,
                0, 0, 0, (short) 4, 2, (short) 6, 5));
        // 设置注释内容
        comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
        // 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
        comment.setAuthor("jimi");

        // 产生表格标题行
        HSSFRow row = sheet.createRow(0);
        for (short i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(headerStyle);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }

        // 遍历集合数据，产生数据行
        if (CollectionUtils.isNotEmpty(dataList)){
            int index = 0;
            for (T t : dataList){

                index++;

                //创建新行
                row = sheet.createRow(index);

                for (int i = 0; i < fields.length; i++) {

                    HSSFCell cell = row.createCell(i);
                    cell.setCellStyle(style2);

                    String valueStr = null;
                    String fieldName = fields[i];

                    try {
                        /*
                        getDeclaredField是可以获取一个类的所有字段.
                        getField只能获取类的public 字段.
                         */
                        Field field = t.getClass().getDeclaredField(fieldName);
                        if (!field.isAccessible()) {
                            field.setAccessible(true);
                        }
                        Object value = field.get(t);
                        if (value instanceof Date){
                            Date date = (Date) value;
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            valueStr = sdf.format(date);
                        } else if (value instanceof byte[]) {
                            // 有图片时，设置行高为60px;
                            row.setHeightInPoints(60);
                            // 设置图片所在列宽度为80px,注意这里单位的一个换算
                            sheet.setColumnWidth(i, (short) (35.7 * 80));
                            // sheet.autoSizeColumn(i);
                            byte[] bsValue = (byte[]) value;
                            HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0,
                                    1023, 255, (short) 6, index, (short) 6, index);
                            anchor.setAnchorType(2);
                            patriarch.createPicture(anchor, wb.addPicture(
                                    bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG));
                        }else {
                            valueStr = value.toString();
                        }
                        // 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
                        if (valueStr != null) {
                            Pattern p = Pattern.compile("^//d+(//.//d+)?$");
                            Matcher matcher = p.matcher(valueStr);
                            if (matcher.matches()) {
                                // 是数字当作double处理
                                cell.setCellValue(Double.parseDouble(valueStr));
                            } else {
                                HSSFRichTextString richString = new HSSFRichTextString(
                                        valueStr);
                                HSSFFont font3 = wb.createFont();
                                font3.setColor(HSSFColor.BLACK.index);
                                richString.applyFont(font3);
                                cell.setCellValue(richString);
                            }
                        }
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        try {
            wb.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取Excel表格表头的内容
     * @param is
     * @return String 表头内容的数组
     */
    public String[] readExcelTitle(InputStream is) {
        try {
            fs = new POIFSFileSystem(is);
            wb = new HSSFWorkbook(fs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sheet = wb.getSheetAt(0);
        row = sheet.getRow(0);
        // 标题总列数
        int colNum = row.getPhysicalNumberOfCells();
        log.debug("colNum:" + colNum);
        String[] title = new String[colNum];
        for (int i = 0; i < colNum; i++) {
            //title[i] = getStringCellValue(row.getCell((short) i));
            title[i] = getCellFormatValue(row.getCell(i));
        }
        return title;
    }

    /**
     * 读取Excel数据内容
     * @param is
     * @return Map 包含单元格数据内容的Map对象
     */
    public Map<Integer, List<String>> readExcelRowContent(InputStream is, Integer sheetIndex, int rows, int cols) throws LgPlatBusinessException {
        Map<Integer, List<String>> content = new HashMap<Integer, List<String>>();
        List<String> rowRecord = new ArrayList<>();
        try {
            fs = new POIFSFileSystem(is);
            wb = new HSSFWorkbook(fs);  //此处表示，只支持解析2003版excel文件;解析2007版的是使用XSSFWorkbook类
        } catch (IOException e) {
            e.printStackTrace();
        }
        //log.debug(" ######工作表数量=###### "+wb.getNumberOfSheets());
        sheet = wb.getSheetAt((sheetIndex==null ? 0 : sheetIndex));
        // 得到总行数
        int rowNum = sheet.getLastRowNum();
        //如果row
        if(rows <= 0){
            rows = rowNum + 1;
        }
        log.debug(" ######表行数=###### " + rowNum);
        row = sheet.getRow(0);
        int colNum = row.getPhysicalNumberOfCells();
        log.debug(" ######表列数=###### " + colNum);
        if(cols > 0){
            if(colNum != cols){
                throw new LgPlatBusinessException("excel文件内容列数不符要求");
            }
        }else{
            cols = colNum;
        }
        // 正文内容应该从第二行开始,第一行为表头的标题
        for (int i = 1; i <= rowNum && i < rows; i++) {
            row = sheet.getRow(i);
            if(row != null) {
                int j = 0;
                while (j < colNum & j < cols) {
                    // 每个单元格的数据内容用"-"分割开，以后需要时用String类的replace()方法还原数据
                    // 也可以将每个单元格的数据设置到一个javabean的属性中，此时需要新建一个javabean
                    // str += getStringCellValue(row.getCell((short) j)).trim() +
                    // "-";
                    log.debug("data=" + row.getCell(j));
                    String column = getCellFormatValue(row.getCell(j));
                    rowRecord.add(column);
                    j++;
                }
                content.put(i - 1, rowRecord);
                rowRecord = new ArrayList<>();
            }
        }
        return content;
    }

    /**
     * 获取单元格数据内容为字符串类型的数据
     *
     * @param cell Excel单元格
     * @return String 单元格数据内容
     */
    private String getStringCellValue(HSSFCell cell) {
        String strCell = "";
        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_STRING:
                strCell = cell.getStringCellValue();
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                strCell = String.valueOf(cell.getNumericCellValue());
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN:
                strCell = String.valueOf(cell.getBooleanCellValue());
                break;
            case HSSFCell.CELL_TYPE_BLANK:
                strCell = "";
                break;
            default:
                strCell = "";
                break;
        }
        if (strCell.equals("") || strCell == null) {
            return "";
        }
        if (cell == null) {
            return "";
        }
        return strCell;
    }

    /**
     * 获取单元格数据内容为日期类型的数据
     *
     * @param cell
     *            Excel单元格
     * @return String 单元格数据内容
     */
    private String getDateCellValue(HSSFCell cell) {
        String result = "";
        try {
            int cellType = cell.getCellType();
            if (cellType == HSSFCell.CELL_TYPE_NUMERIC) {
                Date date = cell.getDateCellValue();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                //日历得到的月是从0开始，所以加1
                result = (calendar.get(calendar.YEAR) + 1900) + "-" + (calendar.get(calendar.MONTH) + 1)
                        + "-" + calendar.get(calendar.DAY_OF_MONTH);
            } else if (cellType == HSSFCell.CELL_TYPE_STRING) {
                String date = getStringCellValue(cell);
                result = date.replaceAll("[年月]", "-").replace("日", "").trim();
            } else if (cellType == HSSFCell.CELL_TYPE_BLANK) {
                result = "";
            }
        } catch (Exception e) {
            log.error("日期格式不正确!");
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据HSSFCell类型设置数据
     * @param cell
     * @return
     */
    private String getCellFormatValue(HSSFCell cell) {
        String cellvalue = "";
        if (cell != null) {
            // 判断当前Cell的Type
            switch (cell.getCellType()) {
                // 如果当前Cell的Type为NUMERIC
                case HSSFCell.CELL_TYPE_NUMERIC:
                case HSSFCell.CELL_TYPE_FORMULA: {
                    // 判断当前的cell是否为Date
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        // 如果是Date类型则，转化为Data格式

                        //方法1：这样子的data格式是带时分秒的：2011-10-12 0:00:00
                        //cellvalue = cell.getDateCellValue().toLocaleString();

                        //方法2：这样子的data格式是不带带时分秒的：2011-10-12
                        Date date = cell.getDateCellValue();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        cellvalue = sdf.format(date);

                    }
                    // 如果是纯数字
                    else {
                        // 取得当前Cell的数值
                        cellvalue = String.valueOf((long)cell.getNumericCellValue());
                    }
                    break;
                }
                // 如果当前Cell的Type为STRIN
                case HSSFCell.CELL_TYPE_STRING:
                    // 取得当前的Cell字符串
                    cellvalue = cell.getRichStringCellValue().getString();
                    break;
                // 默认的Cell值
                default:
                    cellvalue = "";
            }
        } else {
            cellvalue = "";
        }
        return cellvalue;

    }
}
