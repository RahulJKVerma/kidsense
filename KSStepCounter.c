/*
 * src/main.c
 * Creates a Window and two TextLayers to display instructions and feedback 
 * from the background worker. 
 */

#include <pebble.h>
#define TAP_NOT_DATA false
#define WORKER_TICKS 0

 //DHEERA
 #define ACCEL_STEP_MS 50
 #define NUM_DISCS 1
 #define DISC_DENSITY 0.25
 #define MATH_PI 3.141592653589793238462
 #define ACCEL_RATIO 0.05

 //DHEERA
 typedef struct Vec2d {
  double x;
  double y;
} Vec2d;


 //DHEERA
 typedef struct Disc {
#ifdef PBL_COLOR
  GColor color;
#endif
  Vec2d pos;
  Vec2d vel;
  double mass;
  double radius;
} Disc;


static Disc disc_array[NUM_DISCS];
static Window *s_main_window;
static TextLayer *s_output_layer, *s_ticks_layer;
uint32_t num_samples = 3;

//DHEERA
static GRect window_frame;
static Layer *s_disc_layer;

//DHEERA
static double disc_calc_mass(Disc *disc) {
  return MATH_PI * disc->radius * disc->radius * DISC_DENSITY;
}

//DHEERA
static void disc_update(Disc *disc) {
  double e = 0.5;

  if ((disc->pos.x - disc->radius < 0 && disc->vel.x < 0)
    || (disc->pos.x + disc->radius > window_frame.size.w && disc->vel.x > 0)) {
    disc->vel.x = -disc->vel.x * e;
  }

  if ((disc->pos.y - disc->radius < 0 && disc->vel.y < 0)
    || (disc->pos.y + disc->radius > window_frame.size.h && disc->vel.y > 0)) {
    disc->vel.y = -disc->vel.y * e;
  }

  disc->pos.x += disc->vel.x;
  disc->pos.y += disc->vel.y;
}

//DHEERA
static void disc_draw(GContext *ctx, Disc *disc) {
#ifdef PBL_COLOR
  graphics_context_set_fill_color(ctx, disc->color);
#else
  graphics_context_set_fill_color(ctx, GColorWhite);
#endif
  graphics_fill_circle(ctx, GPoint(disc->pos.x, disc->pos.y), disc->radius);
}

//DHEERA
static void disc_apply_force(Disc *disc, Vec2d force) {
  disc->vel.x += force.x / disc->mass;
  disc->vel.y += force.y / disc->mass;
}

//DHEERA
static void disc_apply_accel(Disc *disc, AccelData accel) {
  disc_apply_force(disc, (Vec2d) {
    .x = accel.x * ACCEL_RATIO,
    .y = -accel.y * ACCEL_RATIO
  });
}

//DHEERA
static void disc_layer_update_callback(Layer *me, GContext *ctx) {
  for (int i = 0; i < NUM_DISCS; i++) {
    disc_draw(ctx, &disc_array[i]);
  }
}

//DHEERA
static void disc_init(Disc *disc,int i) {
  static double next_radius = 40;

  GRect frame = window_frame;
  disc->pos.x = frame.size.w/4*i;
  disc->pos.y = frame.size.h/4*i;
  disc->vel.x = 0;
  disc->vel.y = 0;
  disc->radius = next_radius;
  disc->mass = disc_calc_mass(disc);
#ifdef PBL_COLOR
  disc->color = GColorFromRGB(rand() % 255, rand() % 255, rand() % 255);
#endif
  next_radius += 0.5;
}

static void worker_message_handler(uint16_t type, AppWorkerMessage *data) {
  if (type == WORKER_TICKS) { 
    // Read ticks from worker's packet
    int ticks = data->data0;

    // Show to user in TextLayer
    static char s_buffer[32];
    snprintf(s_buffer, sizeof(s_buffer), "%d background ticks", ticks);
    text_layer_set_text(s_ticks_layer, s_buffer);
  }
}

static void select_click_handler(ClickRecognizerRef recognizer, void *context) {
  // Check to see if the worker is currently active
  bool running = app_worker_is_running();

  // Toggle running state
  AppWorkerResult result;
  if (running) {
    result = app_worker_kill();

    if (result == APP_WORKER_RESULT_SUCCESS) {
      text_layer_set_text(s_ticks_layer, "Worker stopped!");
    } else {
      text_layer_set_text(s_ticks_layer, "Error killing worker!");
    }
  } else {
    result = app_worker_launch();

    if (result == APP_WORKER_RESULT_SUCCESS) {
      text_layer_set_text(s_ticks_layer, "Worker launched!");
    } else {
      text_layer_set_text(s_ticks_layer, "Error launching worker!");
    }
  }

  APP_LOG(APP_LOG_LEVEL_INFO, "Result: %d", result);
}

static void click_config_provider(void *context) {
  window_single_click_subscribe(BUTTON_ID_SELECT, select_click_handler);
}


//DHEERA
static void timer_callback(void *data) {
  AccelData accel = (AccelData) { .x = 0, .y = 0, .z = 0 };
  accel_service_peek(&accel);

  for (int i = 0; i < NUM_DISCS; i++) {
    Disc *disc = &disc_array[i];
    disc_apply_accel(disc, accel);
    disc_update(disc);
  }

  layer_mark_dirty(s_disc_layer);

  app_timer_register(ACCEL_STEP_MS, timer_callback, NULL);
}


static void main_window_load(Window *window) {
  Layer *window_layer = window_get_root_layer(window);
  GRect window_bounds = layer_get_bounds(window_layer);

  //DHEERA
  GRect frame = window_frame = layer_get_frame(window_layer);
  s_disc_layer = layer_create(frame);
  layer_set_update_proc(s_disc_layer, disc_layer_update_callback);
  layer_add_child(window_layer, s_disc_layer);
  for (int i = 0; i < NUM_DISCS; i++) {
    disc_init(&disc_array[i],i);
  }

  // Create UI
  s_output_layer = text_layer_create(GRect(0, 0, window_bounds.size.w, window_bounds.size.h/3));
  text_layer_set_font(s_output_layer, fonts_get_system_font(FONT_KEY_GOTHIC_24));
  text_layer_set_text(s_output_layer, "No data yet.");
  text_layer_set_overflow_mode(s_output_layer, GTextOverflowModeWordWrap);
  layer_add_child(window_layer, text_layer_get_layer(s_output_layer));
/*
  //text_layer_set_text(s_output_layer, "Use SELECT to start/stop the background worker.");
  text_layer_set_text_alignment(s_output_layer, GTextAlignmentCenter);
  layer_add_child(window_layer, text_layer_get_layer(s_output_layer));


  s_ticks_layer = text_layer_create(GRect(5, 5, window_bounds.size.w, 40));
  text_layer_set_text(s_ticks_layer, "No data yet.");
  text_layer_set_text_alignment(s_ticks_layer, GTextAlignmentLeft);
  layer_add_child(window_layer, text_layer_get_layer(s_ticks_layer));
*/
}


static void main_window_unload(Window *window) {
  // Destroy UI
  text_layer_destroy(s_output_layer);
  text_layer_destroy(s_ticks_layer);
}

//DHEERA
static void data_handler(AccelData *data, uint32_t num_samples) {
 // Long lived buffer
  static char s_buffer[128];
  char x='a';
  APP_LOG(APP_LOG_LEVEL_INFO, "In DataHandler=%c", x);

  // Compose string of all data for 3 samples
  snprintf(s_buffer, sizeof(s_buffer), 
    "N X,Y,Z\n0 %d,%d,%d\n1 %d,%d,%d\n2 %d,%d,%d", 
    data[0].x, data[0].y, data[0].z, 
    data[1].x, data[1].y, data[1].z, 
    data[2].x, data[2].y, data[2].z
  );
  
    APP_LOG(APP_LOG_LEVEL_INFO,  "N X,Y,Z\n0 %d,%d,%d\n1 %d,%d,%d\n2 %d,%d,%d", 
    data[0].x, data[0].y, data[0].z, 
    data[1].x, data[1].y, data[1].z, 
    data[2].x, data[2].y, data[2].z);

  //Show the data
  text_layer_set_text(s_output_layer, s_buffer);
}

//DHEERA
static void tap_handler(AccelAxisType axis, int32_t direction) {
  switch (axis) {
  case ACCEL_AXIS_X:
    if (direction > 0) {
      text_layer_set_text(s_output_layer, "X axis positive.");
    } else {
      text_layer_set_text(s_output_layer, "X axis negative.");
    }
    break;
  case ACCEL_AXIS_Y:
    if (direction > 0) {
      text_layer_set_text(s_output_layer, "Y axis positive.");
    } else {
      text_layer_set_text(s_output_layer, "Y axis negative.");
    }
    break;
  case ACCEL_AXIS_Z:
    if (direction > 0) {
      text_layer_set_text(s_output_layer, "Z axis positive.");
    } else {
      text_layer_set_text(s_output_layer, "Z axis negative.");
    }
    break;
  }
}

static void init(void) {
  // Setup main Window
  s_main_window = window_create();

  //DHEERA
  window_set_background_color(s_main_window, GColorBlack);

  window_set_click_config_provider(s_main_window, click_config_provider);
  window_set_window_handlers(s_main_window, (WindowHandlers) {
    .load = main_window_load,
    .unload = main_window_unload,
  });
  window_stack_push(s_main_window, true);

  //DHEERA
   // Use tap service? If not, use data service
  if (TAP_NOT_DATA) {
    // Subscribe to the accelerometer tap service
    accel_tap_service_subscribe(tap_handler);
  } else {
    

    // Subscribe to the accelerometer data service
    int num_samples = 3;
    APP_LOG(APP_LOG_LEVEL_INFO, "IN ELSE TAP_NOT_DATA=%d", num_samples);
    accel_data_service_subscribe(num_samples, data_handler);
    APP_LOG(APP_LOG_LEVEL_INFO, "IN ELSE !!TAP_NOT_DATA=%d", num_samples+1);

    // Choose update rate
    accel_service_set_sampling_rate(ACCEL_SAMPLING_10HZ);
  }


  //DHEERA
 //accel_data_service_subscribe(0, NULL);
  //accel_data_service_subscribe(num_samples, data_handler);

//CALL THIS METHOD WHEN YOU WANT TO HAVE A CALLBACK IN REGULAR INTERVALS CHECKING IF THE USER IS STAGNANT OR NOT
  app_timer_register(ACCEL_STEP_MS, timer_callback, NULL);


  // Subscribe to Worker messages
  app_worker_message_subscribe(worker_message_handler); 
}

static void deinit(void) {


  //DHEERA
  accel_data_service_unsubscribe();

  // Destroy main Window
  window_destroy(s_main_window);

  //DHEERA
  if (TAP_NOT_DATA) {
    accel_tap_service_unsubscribe();
  } else {
    accel_data_service_unsubscribe();
  }

/*
  // No more worker updates
  app_worker_message_unsubscribe();
  */
}

int main(void) {
  init();
  app_event_loop();
  deinit();
}
