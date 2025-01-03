
export const formatExtensionValue = (value: any) => {
    if (typeof value === 'object' && value !== null && 'name' in value && 'value' in value) {
        return value.value;
    }

    if (typeof value === 'boolean') {
        return value;
    }

    return value;
};

export const setNestedValue = (object: any, nestedPath: string, newValue: any): any => {
    const [firstKey, ...remainingPathSegments] = nestedPath.split('.');
    if (remainingPathSegments.length === 0) {
        return { ...object, [firstKey]: newValue };
    }
    return {
        ...object,
        [firstKey]: setNestedValue(object[firstKey] || {}, remainingPathSegments.join('.'), newValue),
    };
};

export const setAge = (birthDate: Date, samplingDate: Date): number => {
    if (!birthDate || !samplingDate) {
        return 0;
    }
    let age = samplingDate.getFullYear() - birthDate.getFullYear();
    const monthDifference = samplingDate.getMonth() - birthDate.getMonth()
    if (monthDifference < 0 || (monthDifference === 0 && samplingDate.getDate() < birthDate.getDate())) {
        age--;
    }
    return age;
};
