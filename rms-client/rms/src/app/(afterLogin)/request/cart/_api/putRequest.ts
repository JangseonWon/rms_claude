import {Request} from "@/model/Request"

export async function putRequest(requests: Request[]) {
    return await fetch(`/w-api/cart-service/requests`, {
        method: 'PUT',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(requests),
        credentials: 'include',
        cache: 'no-store'
    });
}