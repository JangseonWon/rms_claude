export async function getStatisticsRequest() {
    return await fetch(`/w-api/dashboard-service/statistics`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}