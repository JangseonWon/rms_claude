export async function getAlarmByUser() {
    const res = await fetch(`/w-api/post-service/alarms/post`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) {
        const errorMessage = await res.text();
        // alert(`Error: ${errorMessage}`);
    }
    return res
}