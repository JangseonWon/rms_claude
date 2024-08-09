import {Paging} from "@/model/Paging";

export async function getRequests(search: Paging) {
    return await fetch(`/w-api/order-service/requests`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(search),
        credentials: 'include',
        cache: 'no-store'
    });
}