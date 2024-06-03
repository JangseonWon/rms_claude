export async function getRequestOrders(pageSize: number, pageNumber: number) {
    const res = await fetch(`/w-api/order-service/requests?status=order`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            filter: [{
            }],
            sort_by: "last_modify_at",
            asc: false,
            size: pageSize,
            page: pageNumber
        }),
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) throw new Error('Failed to fetch data');

    return res;
}