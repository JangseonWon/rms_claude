export const formatDateLocal = (date: Date) => {
    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');
    return `${yyyy}-${mm}-${dd}`;
};

export const getStringDateFromComponents = (year?: number, month?: number, day?: number): string | undefined => {
    if (!year || !month || !day) return undefined;
    const date = new Date(year, month - 1, day);
    return formatDate(date);
};

export const getDateFromComponents = (year?: number, month?: number, day?: number): Date | undefined => {
    if (!year || !month || !day) return undefined;
    return new Date(year, month - 1, day);
}

const formatDate = (date: Date): string => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
};