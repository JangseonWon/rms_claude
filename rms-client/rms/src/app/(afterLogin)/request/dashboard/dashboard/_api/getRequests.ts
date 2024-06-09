export async function getRequests(pageSize: number, pageNumber: number) {
    const res = await fetch(`/w-api/dashboard-service/requests`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            page: {
                size: pageSize,
                number: pageNumber
            }
        }),
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) throw new Error('Failed to fetch data')
    return  res
}