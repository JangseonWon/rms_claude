import {Request} from "@/model/Request"

export async function updateRequest(request: Request) {
    const res = await fetch(`/w-api/cart-service/orders/${request.order_id}/services/${request.service!.id}/samples/${request.sample!.id}`, {
        method: 'PATCH',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(request),
        credentials: 'include',
        cache: 'no-store'
    });
    return  res
}