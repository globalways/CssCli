package com.globalways.csscli.tools;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.openbeans.BeanInfo;
import com.googlecode.openbeans.Introspector;
import com.googlecode.openbeans.PropertyDescriptor;


/**
 * bean相关操作
 * @author wyp E-mail:onebyte@qq.com
 * @version Time: 2015年5月8日 下午5:22:44
 */
public class BeanUtils {

	/**
	 * bean转map
	 * @param bean
	 * @return
	 */
	public static Map<String, Object> beanToMap(Object bean) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			BeanInfo info = Introspector.getBeanInfo(bean.getClass());
			PropertyDescriptor[] pd = info.getPropertyDescriptors();
			for (int i = 0; i < pd.length; i++) {
				PropertyDescriptor descriptor = pd[i];
				String name = descriptor.getName();
				if (!name.equals("class")) {
					Method readMethod = descriptor.getReadMethod();
					Object result = readMethod.invoke(bean, new Object[0]);
					if (result != null) {
						map.put(name, result);
					} else {
						map.put(name, "");
					}
				}
			}
		}
		catch (Exception e) {
			
		}
		return map;
	}
}
