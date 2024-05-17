export async function getStatisticsRequest() {
    const res = await fetch(`/w-api/dashboard-service/statistics`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) throw new Error('Failed to fetch data')
    return res
}