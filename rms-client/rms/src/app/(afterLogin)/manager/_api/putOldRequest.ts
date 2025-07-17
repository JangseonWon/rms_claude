export async function putOldRequest(request: object) {
    return await fetch(`/w-api/management-service/requests`, {
        method: 'PUT',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(request),
        credentials: 'include',
        cache: 'no-store'
    });
}