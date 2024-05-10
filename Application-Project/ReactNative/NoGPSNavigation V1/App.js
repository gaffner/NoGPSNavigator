import React from "react";
import { StyleSheet, View, Text } from "react-native";
import Mapbox from '@rnmapbox/maps';

 
function App () {
  return (
    <View style={styles.page}>
    {/* <View style={styles.container}>
      <Mapbox.MapView style={styles.map} />
    </View> */}
    <Text>hello world</Text>
  </View>
  );
};
 
const styles = StyleSheet.create({
  page: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  container: {
    height: 300,
    width: 300,
  },
  map: {
    flex: 1
  }
});
 
export default App;