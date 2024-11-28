export async function getAlarmCountByUser() {
    return await fetch(`/w-api/post-service/alarms/count`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}