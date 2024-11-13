export async function getExtension(extensionId: string) {
    return await fetch(`/w-api/management-service/extensions/${extensionId}`, {
        method: 'GET',
        headers: {
            "Content-Type": "application/json",
        },
        credentials: 'include',
        cache: 'no-store'
    });
}