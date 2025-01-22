import {Request} from "@/model/Request"

export async function putRequest(request: Request) {
    return await fetch(`/w-api/catalog-service/requests`, {
        method: 'PUT',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(request),
        credentials: 'include',
        cache: 'no-store'
    });
}