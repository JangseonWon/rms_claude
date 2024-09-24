import OrderInfo from "@/app/(afterLogin)/request/order/_component/OrderInfo";
import {Suspense} from "react";

export default function Page() {
  return (
      <Suspense>
        <OrderInfo/>
      </Suspense>
  );
}