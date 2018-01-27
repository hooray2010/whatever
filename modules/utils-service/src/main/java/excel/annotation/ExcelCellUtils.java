package excel.annotation;

import excel.model.ExcelMetaData;
import org.assertj.core.util.Lists;
import org.springframework.util.CollectionUtils;
import excel.exception.ExcelException;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;


public class ExcelCellUtils<T extends Object> {
  
  private ExcelOrderedCellCache orderedCellCache = ExcelOrderedCellCache.instance;
  
  /**
   * 按优先级顺序，获取声明了{@code @ExcelCell}注解的元数据;
   *
   * @param t
   * @return
   */
  public List<ExcelMetaData> getOrderedMedaData(T t) {
    List<ExcelMetaData> excelMetaDatas = Lists.newArrayList();
    
    Class clz = t.getClass();
    
    while (clz != null) {
      for (Field field : clz.getDeclaredFields()) {
        
        ExcelMetaData metaData = new ExcelMetaData();
        
        //获取Excel注解
        ExcelCell annotation = field.getDeclaredAnnotation(ExcelCell.class);
        if (annotation != null) {
          metaData.setExcelCell(annotation);
        } else {
          continue;
        }
        
        metaData.setField(field);
        
        field.setAccessible(true);
        
        //取值
        try {
          metaData.setValue(field.get(t));
        } catch (IllegalAccessException e) {
          throw new ExcelException("反射获取Field的值异常", e);
        }
        excelMetaDatas.add(metaData);
      }
      
      clz = clz.getSuperclass();
    }
    
    excelMetaDatas.sort(new Comparator<ExcelMetaData>() {
      @Override
      public int compare(ExcelMetaData p1, ExcelMetaData p2) {
        return priorityCompare(p1.getExcelCell(), p2.getExcelCell());
      }
    });
    
    
    return excelMetaDatas;
  }
  
  /**
   * 按优先级顺序，获取声明了{@code @ExcelCell}注解的属性值;
   *
   * @param t
   * @return
   */
  public List<Object> getOrderedValues(T t) {
    List<Field> fields = getOrderedFields(t);
    
    List<Object> values = Lists.newArrayList();
    if (! CollectionUtils.isEmpty(fields)) {
      for (Field field : fields) {
        field.setAccessible(true);
        try {
          values.add(field.get(t));
        } catch (IllegalAccessException e) {
          throw new ExcelException("反射获取Field的值异常", e);
        }
      }
    }
    
    return values;
  }
  
  /**
   * 按优先级顺序，获取声明了{@code @ExcelCell}注解的属性的 cellTitle 值;
   *
   * @param t
   * @return
   */
  public List<String> getOrderedCellTitle(T t) {
    List<ExcelCell> cells = getOrderedExcelCells(t);
    
    List<String> cellTitles = Lists.newArrayList();
    if (! CollectionUtils.isEmpty(cells)) {
      for (ExcelCell cell : cells) {
        cellTitles.add(cell.cellTitle());
      }
    }
    
    return cellTitles;
  }
  
  /**
   * 按优先级顺序，获取声明了{@code @ExcelCell}注解的属性的 cellDesc 值;
   *
   * @param t
   * @return
   */
  public List<String> getOrderedCellDesc(T t) {
    List<ExcelCell> cells = getOrderedExcelCells(t);
    
    List<String> cellTitles = Lists.newArrayList();
    if (! CollectionUtils.isEmpty(cells)) {
      for (ExcelCell cell : cells) {
        cellTitles.add(cell.cellDesc());
      }
    }
    
    return cellTitles;
  }
  
  /**
   * 按优先级顺序，获取声明了{@code @ExcelCell}注解的属性;
   * <p>
   * 会向上遍历父类
   *
   * @param t
   * @return
   */
  private List<ExcelCell> getOrderedExcelCells(T t) {
    if (! CollectionUtils.isEmpty(orderedCellCache.getOrderedCells(t.getClass()))) {
      return orderedCellCache.getOrderedCells(t.getClass());
    }
    
    List<ExcelCell> cells = Lists.newArrayList();
    Class clz = t.getClass();
    
    while (clz != null) {
      for (Field field : clz.getDeclaredFields()) {
        ExcelCell annotation = field.getDeclaredAnnotation(ExcelCell.class);
        if (annotation != null) {
          cells.add(annotation);
        }
      }
      
      clz = clz.getSuperclass();
    }
    
    cells.sort(new Comparator<ExcelCell>() {
      @Override
      public int compare(ExcelCell o1, ExcelCell o2) {
        return priorityCompare(o1, o2);
      }
    });
    orderedCellCache.addOrderedCells(t.getClass(), cells);
    
    return cells;
  }
  
  
  public List<Field> getOrderedFields(T t) {
    if (! CollectionUtils.isEmpty(orderedCellCache.getOrderedFields(t.getClass()))) {
      return orderedCellCache.getOrderedFields(t.getClass());
    }
    
    List<Field> fields = Lists.newArrayList();
    
    Class clz = t.getClass();
    while (clz != null) {
      for (Field field : clz.getDeclaredFields()) {
        ExcelCell annotation = field.getDeclaredAnnotation(ExcelCell.class);
        if (annotation != null) {
          fields.add(field);
        }
      }
      
      clz = clz.getSuperclass();
    }
    
    fields.sort(new Comparator<Field>() {
      @Override
      public int compare(Field o1, Field o2) {
        ExcelCell annotation1 = o1.getDeclaredAnnotation(ExcelCell.class);
        ExcelCell annotation2 = o2.getDeclaredAnnotation(ExcelCell.class);
        return priorityCompare(annotation1, annotation2);
      }
    });
    orderedCellCache.addOrderedFields(t.getClass(), fields);
    
    return fields;
    
  }
  
  private int priorityCompare(ExcelCell o1, ExcelCell o2) {
    return excelColStrToNum(o1.priority()) - excelColStrToNum(o2.priority());
  }
  
  private static int excelColStrToNum(String colStr) {
    int priority = 0;
    try {
      priority = Integer.parseInt(colStr);
    } catch (NumberFormatException e) {
      int length = colStr.length();
      int num = 0;
      int result = 0;
      for (int i = 0; i < length; i++) {
        char ch = colStr.charAt(length - i - 1);
        num = (int) (ch - 'A' + 1);
        num *= Math.pow(26, i);
        result += num;
      }
      priority = result;
    }
    return priority;
  }
  
}

















