/**
 * 此包提供职责明晰的Url、Header、QueryParam、RouteParam、FormParam、FormFile的管理，
 * 这些代码从原各个Request中提炼出来，简化了大量的重复代码，
 * 重构了很多相似的代码，使各个类的关系更清楚，职责更明晰
 *
 * 一般地,set相关的都是覆盖性的,add相关的是添加性的
 * 一般地,各个Request中仅持有一个Holder的引用,所有相关的操作都是对这个引用的操作,即：xxxHolder()返回的都是同一个Holder的引用
 */
package top.jfunc.common.http.holder;
