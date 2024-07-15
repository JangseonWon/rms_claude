export async function deleteExtension(serviceId: string | undefined, extensionId: string) {
    return await fetch(`/w-api/management-service/services/${serviceId}/extensions/${extensionId}`, {
        method: 'DELETE',
        credentials: 'include',
        cache: 'no-store'
    });
}