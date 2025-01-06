import {Order} from "@/model/Order";

export async function putRequest(order: Order) {
    return await fetch(`/w-api/catalog-service/requests`, {
        method: 'PUT',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(order),
        credentials: 'include',
        cache: 'no-store'
    });
}