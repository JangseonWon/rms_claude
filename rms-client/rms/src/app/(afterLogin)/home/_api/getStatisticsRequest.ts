export async function getStatisticsRequest() {
    return await fetch(`/w-api/home-service/statistics`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}