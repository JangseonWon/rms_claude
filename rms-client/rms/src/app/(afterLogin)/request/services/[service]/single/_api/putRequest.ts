import {Request} from "@/model/Request";

export async function putRequest(request: Request[], isGroup?: boolean) {
    const url = isGroup ? `/w-api/catalog-service/requests?is_group=true` : `/w-api/catalog-service/requests`;
    return await fetch(url, {
        method: 'PUT',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(request),
        credentials: 'include',
        cache: 'no-store'
    });
}