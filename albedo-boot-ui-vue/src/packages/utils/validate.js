
/**
 * 判断是否为空
 */
export function validateNull(val) {
    if (val instanceof Array) {
        if (val.length == 0) return true;
    } else if (val instanceof Object) {
        if (JSON.stringify(val) === '{}') return true;
    } else {
        if (val == 'null' || val == null || val == 'undefined' || val == undefined || val === '') return true;
    }
    return false;

};

/**
 * 判断是否为空
 */
export function validateNotNull(val) {
  return !validateNull(val);
};
