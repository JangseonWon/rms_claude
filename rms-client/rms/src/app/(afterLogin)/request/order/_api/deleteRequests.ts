import {Request} from "@/model/Request";

export async function deleteRequests(requests: Request[]) {
    return await fetch(`/w-api/order-service/requests`, {
        method: 'DELETE',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(requests),
        credentials: 'include',
        cache: 'no-store'
    });
}