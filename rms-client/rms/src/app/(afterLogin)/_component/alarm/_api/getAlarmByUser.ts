export async function getAlarmByUser() {
    return await fetch(`/w-api/post-service/alarms/post`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}