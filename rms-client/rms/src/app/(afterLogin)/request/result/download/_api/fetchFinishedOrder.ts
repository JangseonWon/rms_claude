export async function fetchFinishedOrder(userId: string | undefined, pageSize: number, pageNumber: number) {
    const res = await fetch(`/w-api/order-service/requests?status=confirm`, {
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